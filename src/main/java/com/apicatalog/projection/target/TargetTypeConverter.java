package com.apicatalog.projection.target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;

public class TargetTypeConverter implements TargetAdapter {

	final Logger logger = LoggerFactory.getLogger(TargetTypeConverter.class);

	final Class<?> sourceClass;
	
	final Class<?> sourceComponentClass;

	final Class<?> targetClass;
	
	final Class<?> targetComponentClass;
	
	final TypeAdapters typeAdapters;
	
	public TargetTypeConverter(TypeAdapters typeAdapters, Class<?> sourceClass, Class<?> sourceComponentClass, Class<?> targetClass, Class<?> targetComponentClass) {
		this.sourceClass = sourceClass;
		this.sourceComponentClass = sourceComponentClass;
		
		this.targetClass = targetClass;
		this.targetComponentClass = targetComponentClass;
		
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object forward(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError {
		logger.debug("Construct {} from {}, depth = {}", targetClass.getSimpleName(), object, queue.length());

		return typeAdapters.convert(targetClass, targetComponentClass, object);
	}

	@Override
	public Object backward(Object object, ContextObjects context) throws ProjectionError {
		logger.debug("Deconstruct {} from {}", sourceClass.getSimpleName(), object);
		
		return typeAdapters.convert(sourceClass, sourceComponentClass, object);
	}

}

