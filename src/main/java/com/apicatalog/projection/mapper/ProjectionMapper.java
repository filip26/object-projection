package com.apicatalog.projection.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.property.ProjectionProperty;

public class ProjectionMapper {

	final Logger logger = LoggerFactory.getLogger(ProjectionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	final PropertyMapper propertyMapper;
	
	public ProjectionMapper(ProjectionFactory factory) {
		this.factory = factory;
		this.typeAdapters = new TypeAdapters();
		this.propertyMapper = new PropertyMapper(factory, typeAdapters);
	}
	
	public <P> Projection<P> getMapping(final Class<P> targetProjectionClass) {

		if (targetProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		
		// ignore unannotated classes
		if (!targetProjectionClass.isAnnotationPresent(com.apicatalog.projection.annotation.Projection.class)) {
			return null;
		}
		
		logger.debug("Scan {}", targetProjectionClass.getCanonicalName());
		
		final com.apicatalog.projection.annotation.Projection projectionAnnotation = targetProjectionClass.getAnnotation(com.apicatalog.projection.annotation.Projection.class);
		
		final ProjectionImpl<P> projectionMapping = new ProjectionImpl<>(targetProjectionClass);
		
		final Class<?> defaultSourceClass = Class.class.equals(projectionAnnotation.value()) ? null : projectionAnnotation.value();
		
		final ArrayList<ProjectionProperty> properties = new ArrayList<>();
		
		// check all declared fields
		for (Field field : targetProjectionClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					logger.trace("Skipped {}.{} because is transient or static", targetProjectionClass.getSimpleName(), field.getName());
					continue;
			}
			
			Optional.ofNullable(propertyMapper.getPropertyMapping(field, defaultSourceClass))
					.ifPresent(
							projectionProperty -> {
									logger.trace("Found {}.{} : {}", targetProjectionClass.getSimpleName(), field.getName(), field.getType().getSimpleName());
									properties.add(projectionProperty);
								}
							);				
		}
		
		if (properties.isEmpty()) {
			logger.debug("Ignored {} because has no projected properties", targetProjectionClass.getSimpleName());
			return null;
		}
		
		projectionMapping.setProperties(properties.toArray(new ProjectionProperty[0]));
		
		return projectionMapping;
	}
}
