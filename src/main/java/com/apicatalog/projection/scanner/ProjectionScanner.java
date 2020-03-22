package com.apicatalog.projection.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;

public class ProjectionScanner {

	final Logger logger = LoggerFactory.getLogger(ProjectionScanner.class);
	
	public ProjectionMapping scan(final Class<?> targetProjectionClass) {

		if (targetProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		
		// ignore unannotated classes
		if (!targetProjectionClass.isAnnotationPresent(Projection.class)) {
			return null;
		}
		
		logger.debug("Scan {}", targetProjectionClass.getCanonicalName());
		
		final Projection projectionAnnotation = targetProjectionClass.getAnnotation(Projection.class);
		
		final ProjectionMapping projectionMapping = new ProjectionMapping(targetProjectionClass);
		
		// check all declared fields
		for (Field field : targetProjectionClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					logger.trace("  skipping property {} of {} because is transient or static", field.getName(), targetProjectionClass.getCanonicalName());
					continue;
			}

			final PropertyMapping propertyMapping = newPropertyMapping(projectionAnnotation, field, targetProjectionClass);
			
			if (propertyMapping != null) {
				logger.trace("  found property {}: {}", propertyMapping.getName(), propertyMapping.getTargetClass().getCanonicalName());
				projectionMapping.add(propertyMapping);
			}
		}
		return projectionMapping;
	}
	
	PropertyMapping newPropertyMapping(final Projection projectionAnnotation, final Field field, final Class<?> targetProjectionClass) {
		final PropertyMapping propertyMapping = new PropertyMapping(field.getName());
		
		// is the property annotated? 
		if (field.isAnnotationPresent(Source.class)) {
			
			final Source source = field.getAnnotation(Source.class);
			
			final SourceMapping sourceMapping = newSourceMapping(source, projectionAnnotation, field);

			if (sourceMapping.getObjectClass() == null) {
				logger.warn("Source class is missing. Property {} of {} is ignored. Use @Projection(value=...) or @Source(type=...).", field.getName(), targetProjectionClass.getCanonicalName());
				return null;
			}
			
			propertyMapping.setSources(new SourceMapping[] {sourceMapping});

		} else if (field.isAnnotationPresent(Sources.class) ) {
			
			final Sources sources = field.getAnnotation(Sources.class);
			
			final List<SourceMapping> sourceMappings = new ArrayList<>();
			
			for (Source source : sources.value()) {
				
				final SourceMapping sourceMapping = newSourceMapping(source, projectionAnnotation, field);
				
				if (sourceMapping.getObjectClass() == null) {
					logger.warn("Source class is missing. Property {} of {} is ignored. Use @Projection(value=...) or @Source(type=...).", field.getName(), targetProjectionClass.getCanonicalName());
					continue;
				}
				sourceMappings.add(sourceMapping);
			}
			
			if (sourceMappings.isEmpty()) {
				return null;				
			}
			
			// set sources
			propertyMapping.setSources(sourceMappings.toArray(new SourceMapping[0]));

			// set conversions to apply
			propertyMapping.setFunctions(sources.map());
			
		} else {
			
			final SourceMapping sourceMapping = new SourceMapping();
			
			// set default source object class
			if (Class.class.equals(projectionAnnotation.value())) {
				logger.warn("Source class is missing. Property {} of {} is ignored. Use @Source(...) or @Sources(...) annotations..", field.getName(), targetProjectionClass.getCanonicalName());
				return null;		
			}
			
			sourceMapping.setObjectClass(projectionAnnotation.value());
			
			// set default source object property name -> use the same name
			sourceMapping.setPropertyName(field.getName());
			
			propertyMapping.setSources(new SourceMapping[] {sourceMapping});
		}

		// a collection?
		if (Collection.class.equals(field.getType())) {
			propertyMapping.setItemClass((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
		}
		
		propertyMapping.setTargetClass(field.getType());			
		return propertyMapping;
	}
	
	SourceMapping newSourceMapping(final Source source, final Projection projectionAnnotation, final Field field) {
		
		final SourceMapping sourceMapping = new SourceMapping();
		
		// set default source object class
		if (!Class.class.equals(projectionAnnotation.value())) {
			sourceMapping.setObjectClass(projectionAnnotation.value());
		}
		
		// set default source object property name -> use the same name
		sourceMapping.setPropertyName(field.getName());

		
		// override source property class
		if (!Class.class.equals(source.type())) {
			sourceMapping.setObjectClass(source.type());
		}
		// override source property name
		if (StringUtils.isNotBlank(source.value())) {
			sourceMapping.setPropertyName(source.value());
		}
		// set source object qualifier
		if (StringUtils.isNotBlank(source.qualifier())) {
			sourceMapping.setQualifier(source.qualifier());
		}
		// set conversions to apply
		sourceMapping.setFunctions(source.map());
		
		//TODO optional/nullable?
		return sourceMapping;
	}
	
}
