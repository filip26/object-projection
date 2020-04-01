package com.apicatalog.projection.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectProjection;
import com.apicatalog.projection.ObjectProjectionImpl;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.property.ProjectionProperty;

public class ProjectionMapper {

	final Logger logger = LoggerFactory.getLogger(ProjectionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	final PropertyMapper propertyMapper;
	
	public ProjectionMapper(ProjectionFactory factory) {
		this.factory = factory;
		this.propertyMapper = new PropertyMapper(factory);
		this.typeAdapters = new TypeAdapters();
	}
	
	public <P> ObjectProjection<P> getMapping(final Class<P> targetProjectionClass) {

		if (targetProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		
		// ignore unannotated classes
		if (!targetProjectionClass.isAnnotationPresent(Projection.class)) {
			return null;
		}
		
		logger.debug("Scan {}", targetProjectionClass.getCanonicalName());
		
		final Projection projectionAnnotation = targetProjectionClass.getAnnotation(Projection.class);
		
		final ObjectProjectionImpl<P> projectionMapping = new ObjectProjectionImpl<>(targetProjectionClass);
		
		final Class<?> defaultSourceClass = Class.class.equals(projectionAnnotation.value()) ? null : projectionAnnotation.value();
		
		final ArrayList<ProjectionProperty> properties = new ArrayList<>();
		
		// check all declared fields
		for (Field field : targetProjectionClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					logger.trace("Skipping property {} because is transient or static", field.getName());
					continue;
			}
			
			Optional.ofNullable(propertyMapper.getPropertyMapping(field, defaultSourceClass))
					.ifPresent(
							projectionProperty -> {
									logger.trace("Found property {} : {}", field.getName(), field.getType().getSimpleName());
									properties.add(projectionProperty);
								}
							);				
		}
		
		projectionMapping.setProperties(properties.toArray(new ProjectionProperty[0]));
		
		return projectionMapping;
	}
}
