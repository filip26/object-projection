package com.apicatalog.projection.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.annotation.Embedded;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;

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
		
		final Class<?> defaultSourceClass = Class.class.equals(projectionAnnotation.value()) ? null : projectionAnnotation.value();
		
		// check all declared fields
		for (Field field : targetProjectionClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					logger.trace("  skipping property {} of {} because is transient or static", field.getName(), targetProjectionClass.getCanonicalName());
					continue;
			}
			
			Optional.ofNullable(mapProperty(projectionMapping, field, defaultSourceClass))
					.ifPresent(
							mapping -> {
									logger.trace("  found property {}: {}", mapping.getName(), mapping.getTarget().getTargetClass().getCanonicalName());
									projectionMapping.add(mapping);
								}
							);				
		}
		return projectionMapping;
	}
	
	PropertyMapping mapProperty(final ProjectionMapping projectionMapping, final Field field, final Class<?> defaultSourceClass) {
		
		final PropertyMapping propertyMapping = new PropertyMapping(field.getName());
		propertyMapping.setTarget(mapTarget(field));
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {

			final Optional<SourceMapping> sourceMapping = 
					Optional.ofNullable(
								mapSource(
											projectionMapping,
											field.getAnnotation(Source.class), 
											field,
											defaultSourceClass
										));
			
			if (sourceMapping.isEmpty()) {
				return null;
			}
			
			sourceMapping
				.map(mapping -> new SourceMapping[] {mapping})
				.ifPresent(propertyMapping::setSources);			

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			
			final Sources sources = field.getAnnotation(Sources.class);
			
			final Optional<SourceMapping[]> sourceMappings = 
						Optional.ofNullable( 
									mapSources(
											projectionMapping,
											sources,  
											field,
											defaultSourceClass
											)
										);
			
			if (sourceMappings.isEmpty()) {
				return null;				
			}

			// set sources
			sourceMappings.ifPresent(propertyMapping::setSources);
			
			// set conversions to apply
			if (sources.map() != null && sources.map().length > 0) {
				propertyMapping.setFunctions(sources.map());
			}

		// embedded projection uses global source
		} else if (field.isAnnotationPresent(Embedded.class)) {

		// no action needed?
		} else if (field.isAnnotationPresent(Provided.class) ) {
			logger.trace("  skipping property {} of {} because is marked as provided", field.getName(), projectionMapping.getProjectionClass().getCanonicalName());
			return null;
			
		// direct mapping or a reference 
		} else {

			if (defaultSourceClass == null) {
				logger.warn("Source class is missing. Property {} of {} is ignored. Use @Projection(...) or @Source(...) annotations.", field.getName(), projectionMapping.getProjectionClass().getCanonicalName());
				return null;				
			}

			final SourceMapping sourceMapping = new SourceMapping();
				
			// set default source object class
			sourceMapping.setSourceClass(defaultSourceClass);

			// set default source object property name -> use the same name
			sourceMapping.setPropertyName(field.getName());
			
			// check if field exists and is accessible
			if (!isFieldPresent(sourceMapping.getSourceClass(), sourceMapping.getPropertyName())) {
				logger.warn("Property {} is not accessible or does not exist in {} and is ignored. Use @Source(value=...) to set source property name.", field.getName(), sourceMapping.getSourceClass());
				return null;
			}				

			propertyMapping.setSources(new SourceMapping[] {sourceMapping});			
		}
		return propertyMapping;
	}
	
	TargetMapping mapTarget(Field field) {
		final TargetMapping targetMapping = new TargetMapping();

		targetMapping.setTargetClass(field.getType());

		// a collection?
		if (Collection.class.isAssignableFrom(field.getType())) {
			targetMapping.setItemClass((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
			targetMapping.setReference(targetMapping.getItemClass().isAnnotationPresent(Projection.class));
			
		} else {
			targetMapping.setReference(targetMapping.getTargetClass().isAnnotationPresent(Projection.class));
		}
		
		return targetMapping;
	}
	
	SourceMapping[] mapSources(final ProjectionMapping projectionMapping, final Sources sources, final Field field, final Class<?> defaultSourceClass) {
		
		final List<SourceMapping> sourceMappings = new ArrayList<>();
		
		for (Source source : sources.value()) {
			
			final Optional<SourceMapping> sourceMapping = 
					Optional.ofNullable(
								mapSource(projectionMapping, source, field, defaultSourceClass)
								);

			if (sourceMapping.isEmpty()) {
				continue;
			}
			
			sourceMapping.ifPresent(sourceMappings::add);
		}
		
		return sourceMappings.isEmpty() ? null : sourceMappings.toArray(new SourceMapping[0]);
	}
	
	SourceMapping mapSource(final ProjectionMapping projectionMapping, final Source source, final Field field, final Class<?> defaultSourceClass) {
		
		final SourceMapping sourceMapping = new SourceMapping();
		
		// set default source object class
		if (defaultSourceClass != null) {
			sourceMapping.setSourceClass(defaultSourceClass);
		}
		
		// override source property class
		if (!Class.class.equals(source.type())) {
			sourceMapping.setSourceClass(source.type());
		}
		
		if (sourceMapping.getSourceClass() == null) {
			logger.warn("Source class is missing. Property {} of {} is ignored. Use @Projection(value=...) or @Source(type=...).", field.getName(), projectionMapping.getProjectionClass().getCanonicalName());
			return null;
		}

		// set default source object property name -> use the same name
		sourceMapping.setPropertyName(field.getName());
		
		// override source property name
		if (StringUtils.isNotBlank(source.value())) {
			sourceMapping.setPropertyName(source.value());
		}
		
		// check is source field does exist
		if (!isFieldPresent(sourceMapping.getSourceClass(), sourceMapping.getPropertyName())) {
			logger.warn("Property {} is not accessible or does not exist in {} and is ignored. Use @Source(value=...) to set source property name.", sourceMapping.getPropertyName(), sourceMapping.getSourceClass());
			return null;
		}

		// set source object qualifier
		if (StringUtils.isNotBlank(source.qualifier())) {
			sourceMapping.setQualifier(source.qualifier());
		}
		// set conversions to apply
		if (source.map() != null && source.map().length > 0) {
			sourceMapping.setFunctions(source.map());
		}
		// set optional 
		sourceMapping.setOptional(source.optional());
				
		return sourceMapping;
	}
	
	static boolean isFieldPresent(final Class<?> clazz, final String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);

			return  !Modifier.isStatic(field.getModifiers())
					&& !Modifier.isTransient(field.getModifiers())
					;
			
		} catch (NoSuchFieldException | SecurityException e) {/* ignore */}
		
		return false;
	}
}
