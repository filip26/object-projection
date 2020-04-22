package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.ConversionNotFound;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.target.TargetComposer;
import com.apicatalog.projection.source.SourceType;


public class ProvidedObjectPropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(ProvidedObjectPropertyWriter.class);

	final ProjectionRegistry registry;
	
	Setter targetSetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	TargetComposer composer;
	
	boolean optional;	
	
	public ProvidedObjectPropertyWriter(final ProjectionRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public void write(final ProjectionStack stack, final CompositionContext context) throws CompositionError {		

		if (targetSetter == null) {
			return;
		}
		
		logger.debug("Write {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), objectQualifier, optional, stack.length());
		
		Optional<Object> object = context.get(objectQualifier, targetSetter.getType().getType())
										 .or(() -> context.get(objectQualifier))
										 ;

		if (object.isEmpty()) {
			return;
		}
		
		final Object value = object.get();
		
		if (composer != null) {
			
			object = composer.compose(stack, value, context);
			
			if (object.isPresent()) {
				if (logger.isTraceEnabled()) {
					logger.trace("Set {} to {}", object.get().getClass().getSimpleName(), targetSetter);
				}
				try {
					targetSetter.set(stack.peek(), object.get());
				} catch (ObjectError e) {
					throw new CompositionError("Can not set " + object.get() + " to " + stack.peek().getClass().getCanonicalName() + "." + targetSetter.getName());
				}
			}
			return;
		}
		
		final ObjectType sourceType = Collection.class.isInstance(value)
				? ObjectType.of(value.getClass(), Object.class)
				: ObjectType.of(value.getClass())
				;
		
		try {
			
			Optional<Conversion<Object, Object>> conversion = registry.getTypeConversions().get(sourceType, targetSetter.getType());
			
			if (conversion.isPresent()) {
				object = Optional.ofNullable(conversion.get().convert(value));
			}

			if (object.isPresent()) {
				targetSetter.set(stack.peek(), object.get());
			}
					
		} catch (ConversionNotFound | ConverterError | ObjectError e) {
			throw new CompositionError("Can not compose value for " + stack.peek().getClass().getCanonicalName() + "." + targetSetter.getName() + ".");
		}		
	}
	
	public void setTargetSetter(Setter setter) {
		this.targetSetter = setter;
	}
	
	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}

	@Override
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setObjectQualifier(String objectQualifier) {
		this.objectQualifier = objectQualifier;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public void setComposer(TargetComposer composer) {
		this.composer = composer;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return Collections.emptySet();
	}
	
	@Override
	public String getDependency() {
		return composer != null ? composer.getProjectionName() : null;
	}

	@Override
	public String getName() {
		return targetSetter.getName();
	}
}