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

import com.apicatalog.projection.annotation.Projection;
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
		
		// check all declared fields
		for (Field field : targetProjectionClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					logger.trace("  skipping property {} of {} because is transient or static", field.getName(), targetProjectionClass.getCanonicalName());
					continue;
			}
			
			Optional.ofNullable(mapProperty(projectionAnnotation, field, targetProjectionClass))
					.ifPresent(
							mapping -> {
									logger.trace("  found property {}: {}", mapping.getName(), mapping.getTarget().getTargetClass().getCanonicalName());
									projectionMapping.add(mapping);
								}
							);				
		}
		return projectionMapping;
	}
	
	PropertyMapping mapProperty(final Projection projectionAnnotation, final Field field, final Class<?> targetProjectionClass) {
		
		final PropertyMapping propertyMapping = new PropertyMapping(field.getName());
		
		// is the property annotated? 
		if (field.isAnnotationPresent(Source.class)) {

			final Optional<SourceMapping> sourceMapping = 
					Optional.ofNullable(
								mapSource(
											field.getAnnotation(Source.class), 
											projectionAnnotation, 
											field,
											targetProjectionClass
										));
			
			if (sourceMapping.isEmpty()) {
				return null;
			}
			
			sourceMapping
				.map(mapping -> new SourceMapping[] {mapping})
				.ifPresent(propertyMapping::setSources);			

		} else if (field.isAnnotationPresent(Sources.class) ) {
			
			final Sources sources = field.getAnnotation(Sources.class);
			
			final Optional<SourceMapping[]> sourceMappings = 
						Optional.ofNullable( 
									mapSources(
											sources, 
											projectionAnnotation, 
											field, 
											targetProjectionClass
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
			
		} else {
			
			final SourceMapping sourceMapping = new SourceMapping();
			
			// set default source object class
			if (Class.class.equals(projectionAnnotation.value())) {
				logger.warn("Source class is missing. Property {} of {} is ignored. Use @Projection(...) or @Source(...) annotations.", field.getName(), targetProjectionClass.getCanonicalName());
				return null;		
			}
			
			sourceMapping.setObjectClass(projectionAnnotation.value());

			// set default source object property name -> use the same name
			sourceMapping.setPropertyName(field.getName());

			if (!isFieldPresent(sourceMapping.getObjectClass(), sourceMapping.getPropertyName())) {
				logger.warn("Property {} is not accessible or does not exist in {} and is ignored. Use @Source(value=...) to set source property name.", field.getName(), sourceMapping.getObjectClass());
				return null;
			}
						
			propertyMapping.setSources(new SourceMapping[] {sourceMapping});
		}

		propertyMapping.setTarget(mapTarget(field));
		
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
	
	SourceMapping[] mapSources(final Sources sources, final Projection projectionAnnotation, final Field field, final Class<?> targetProjectionClass) {
		
		final List<SourceMapping> sourceMappings = new ArrayList<>();
		
		for (Source source : sources.value()) {
			
			final Optional<SourceMapping> sourceMapping = 
					Optional.ofNullable(
								mapSource(source, projectionAnnotation, field, targetProjectionClass)
								);

			if (sourceMapping.isEmpty()) {
				continue;
			}
			
			sourceMapping.ifPresent(sourceMappings::add);
		}
		
		return sourceMappings.isEmpty() ? null : sourceMappings.toArray(new SourceMapping[0]);
	}
	
	SourceMapping mapSource(final Source source, final Projection projectionAnnotation, final Field field, final Class<?> targetProjectionClass) {
		
		final SourceMapping sourceMapping = new SourceMapping();
		
		// set default source object class
		if (!Class.class.equals(projectionAnnotation.value())) {
			sourceMapping.setObjectClass(projectionAnnotation.value());
		}
		
		// override source property class
		if (!Class.class.equals(source.type())) {
			sourceMapping.setObjectClass(source.type());
		}
		
		if (sourceMapping.getObjectClass() == null) {
			logger.warn("Source class is missing. Property {} of {} is ignored. Use @Projection(value=...) or @Source(type=...).", field.getName(), targetProjectionClass.getCanonicalName());
			return null;
		}

		// set default source object property name -> use the same name
		sourceMapping.setPropertyName(field.getName());
		
		// override source property name
		if (StringUtils.isNotBlank(source.value())) {
			sourceMapping.setPropertyName(source.value());
		}
		
		// check is source field does exist
		if (!isFieldPresent(sourceMapping.getObjectClass(), sourceMapping.getPropertyName())) {
			logger.warn("Property {} is not accessible or does not exist in {} and is ignored. Use @Source(value=...) to set source property name.", field.getName(), sourceMapping.getObjectClass());
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
