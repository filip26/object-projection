package com.apicatalog.projection.target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ProjectionQueue;

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
	public Object forward(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError {
		logger.debug("Construct {} from {}, depth = {}", targetType, object, queue.length());

		return typeAdapters.convert(targetType, object);
	}

	@Override
	public Object backward(Object object, ContextObjects context) throws ProjectionError {
		logger.debug("Deconstruct {} from {}", sourceType, object);
		
		return typeAdapters.convert(sourceType, object);
	}

}

