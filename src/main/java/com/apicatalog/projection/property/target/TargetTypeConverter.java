package com.apicatalog.projection.property.target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.objects.ObjectType;

public class TargetTypeConverter implements TargetAdapter {

	final Logger logger = LoggerFactory.getLogger(TargetTypeConverter.class);

	final ObjectType sourceType;
	final ObjectType targetType;
	
	final TypeAdapters typeAdapters;
	
	public TargetTypeConverter(TypeAdapters typeAdapters, ObjectType sourceType, ObjectType targetType) {
		this.sourceType = sourceType;
		this.targetType = targetType;
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object forward(ProjectionStack queue, Object object, CompositionContext context) throws ProjectionError {
		logger.debug("Construct {} from {}, depth = {}", targetType, object, queue.length());

		return typeAdapters.convert(targetType, object);
	}

	@Override
	public Object backward(Object object, ExtractionContext context) throws ProjectionError {
		logger.debug("Extract {} from {}", sourceType, object);
		
		return typeAdapters.convert(sourceType, object);
	}
}
