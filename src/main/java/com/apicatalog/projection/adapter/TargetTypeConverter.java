package com.apicatalog.projection.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectType;

@Deprecated(forRemoval = true, since = "0.8")
public class TargetTypeConverter implements ProjectionAdapter {

	final Logger logger = LoggerFactory.getLogger(TargetTypeConverter.class);

	final ObjectType targetType;
	
	final TypeAdaptersLegacy typeAdapters;
	
	public TargetTypeConverter(TypeAdaptersLegacy typeAdapters, ObjectType targetType) {
		this.targetType = targetType;
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object forward(ProjectionStack queue, Object object, CompositionContext context) throws ProjectionError {
		logger.debug("Construct {} from {}, depth = {}", targetType, object, queue.length());

		return typeAdapters.convert(targetType, object);
	}

	@Override
	public Object backward(ObjectType sourceType, Object object, ExtractionContext context) throws ProjectionError {
		logger.debug("Extract {} from {}", sourceType, object);
		
		return typeAdapters.convert(sourceType, object);
	}
}
