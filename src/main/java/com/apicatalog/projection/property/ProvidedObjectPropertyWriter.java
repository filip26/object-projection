package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.property.target.TargetWriter;


public class ProvidedObjectPropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(ProvidedObjectPropertyWriter.class);

	final ProjectionRegistry registry;
	
	TargetWriter targetWriter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;	
	
	public ProvidedObjectPropertyWriter(final ProjectionRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public void write(final ProjectionStack stack, final CompositionContext context) throws ProjectionError {		

		if (targetWriter == null) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Write {}", targetWriter.getType());
		}

		Optional<Object> object = context.get(objectQualifier, targetWriter.getType().getType());

		if (object.isPresent()) {
			
			try {
				Optional<Conversion> conversion = 
						registry.getTypeConversions()
								.get(	
									ObjectType.of(object.get().getClass(), Object.class),
									targetWriter.getType()
									);
				
				if (conversion.isPresent()) {
					object = Optional.ofNullable(conversion.get().convert(object.get()));
				}
				
				if (object.isPresent()) {
					targetWriter.write(stack, context, object.get());
				}
				
			} catch (UnknownConversion | ConverterError e) {
				throw new ProjectionError(e);
			}
		}

		
//		
		
//		logger.debug("Forward {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), objectQualifier, optional, queue.length());
//		
//		Optional<Object> object = context.get(objectQualifier, targetSetter.getType().getType());
//		
//		if (object.isEmpty()) {
//			return;
//		}
//		
//		if (targetAdapter != null) {
//			object = Optional.ofNullable(targetAdapter.forward(queue, object.get(), context));
//		}
//		
//		if (object.isPresent()) {
//			targetSetter.set(queue.peek(), object.get());
//		}
	}
	
	public void setTargetWriter(TargetWriter targetWriter) {
		this.targetWriter = targetWriter;
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
}