package com.apicatalog.projection.property;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.target.TargetExtractor;
import com.apicatalog.projection.source.SourceType;

public final class ProvidedObjectPropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(ProvidedObjectPropertyReader.class);

	final Getter targetGetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;	
	
	TargetExtractor extractor;
	
	protected ProvidedObjectPropertyReader(final Getter targetGetter) {
		this.targetGetter = targetGetter;
	}
	
	public static final ProvidedObjectPropertyReader newInstance(final Getter targetGetter) {
		
		if (targetGetter == null) {
			throw new IllegalArgumentException("Target getter is not set");
		}
		
		return new ProvidedObjectPropertyReader(targetGetter);
	}
	
	@Override
	public void read(final ProjectionStack stack, final ExtractionContext context) throws ExtractionError {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Read {} : {}, qualifier = {}, optional = {}, depth = {}", targetGetter.getName(), targetGetter.getType(), objectQualifier, optional, stack.length());
		}

		Optional<Object> object = Optional.empty();
		
		try {
			
			object = targetGetter.get(stack.peek());
			
		} catch (ObjectError e) {
			throw new ExtractionError("Can not get value of " + targetGetter.getName() + " of " + stack.peek().getClass());
		}
		
		if (object.isEmpty()) {
			return;
		}

		if (extractor != null) {
			final Object value = object.get();
			
			Optional<ObjectType> sourceType = 
							Arrays.stream(context.getAcceptedTypes())
									.filter(type -> type.getType().isInstance(value))
									.findFirst()
									.map(type -> ObjectType.of(type.getType(), type.getComponentType()))
									;
			
			if (sourceType.isPresent()) {
				object = extractor.extract(sourceType.get(), value, context);
			}
		}
		
		if (object.isPresent() ) {
			context.set(objectQualifier, object.get());
		}
	}
	
	public boolean isVisible(final int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setObjectQualifier(final String objectQualifier) {
		this.objectQualifier = objectQualifier;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public void setExtractor(final TargetExtractor extractor) {
		this.extractor = extractor;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return Collections.emptySet();
	}

	@Override
	public String getDependency() {
		return extractor != null ? extractor.getProjectionName() : null;
	}

	@Override
	public String getName() {
		return targetGetter.getName();
	}
}