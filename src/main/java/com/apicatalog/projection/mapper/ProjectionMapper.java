package com.apicatalog.projection.mapper;

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

import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;

public class ProjectionMapper {

	final Logger logger = LoggerFactory.getLogger(ProjectionMapper.class);
	
	public ProjectionMapping getMapping(final Class<?> targetProjectionClass) {

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
					logger.trace("  skipping property {} because is transient or static", field.getName());
					continue;
			}
			
			Optional.ofNullable(getPropertyMapping(field, defaultSourceClass))
					.ifPresent(
							mapping -> {
									logger.trace("  found property {}: {}", mapping.getName(), mapping.getTarget().getTargetClass().getSimpleName());
									projectionMapping.add(mapping);
								}
							);				
		}
		return projectionMapping;
	}
	
	PropertyMapping getPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
				
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			return getSourcePropertyMapping(field, defaultSourceClass);

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			return getSourcesPropertyMapping(field, defaultSourceClass);

		// provided -> global source
		} else if (field.isAnnotationPresent(Provided.class)) {
			final PropertyMapping propertyMapping = new PropertyMapping(field.getName());
			propertyMapping.setTarget(getTargetMapping(field));
			return propertyMapping;
		}
		
		// direct mapping or a reference
		return getDefaultPropertyMapping(field, defaultSourceClass);
	}
	
	PropertyMapping getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final PropertyMapping propertyMapping = new PropertyMapping(field.getName());
		propertyMapping.setTarget(getTargetMapping(field));

		final Sources sources = field.getAnnotation(Sources.class);
		
		final Optional<SourceMapping[]> sourceMappings = 
					Optional.ofNullable( 
								getSourcesMapping(
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
		
		return propertyMapping;
	}
	
	PropertyMapping getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final PropertyMapping propertyMapping = new PropertyMapping(field.getName());
		propertyMapping.setTarget(getTargetMapping(field));

		final Optional<SourceMapping> sourceMapping = 
				Optional.ofNullable(
							getSourceMapping(
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

		return propertyMapping;
	}

	PropertyMapping getDefaultPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		final PropertyMapping propertyMapping = new PropertyMapping(field.getName());
		propertyMapping.setTarget(getTargetMapping(field));

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;				
		}

		final SourceMapping sourceMapping = new SourceMapping();
			
		// set default source object class
		sourceMapping.setSourceClass(defaultSourceClass);

		// set default source object property name -> use the same name
		sourceMapping.setPropertyName(field.getName());
		
		// check if field exists and is accessible
		if (!isFieldPresent(sourceMapping.getSourceClass(), sourceMapping.getPropertyName())) {
			logger.warn("Property {} is not accessible or does not exist in {} and is ignored.", field.getName(), sourceMapping.getSourceClass().getSimpleName());
			return null;
		}				

		propertyMapping.setSources(new SourceMapping[] {sourceMapping});
		return propertyMapping;
	}
	
	TargetMapping getTargetMapping(Field field) {
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
	
	SourceMapping[] getSourcesMapping(final Sources sources, final Field field, final Class<?> defaultSourceClass) {
		
		final List<SourceMapping> sourceMappings = new ArrayList<>();
		
		for (Source source : sources.value()) {
			
			final Optional<SourceMapping> sourceMapping = 
					Optional.ofNullable(
								getSourceMapping(source, field, defaultSourceClass)
								);

			if (sourceMapping.isEmpty()) {
				continue;
			}
			
			sourceMapping.ifPresent(sourceMappings::add);
		}
		
		return sourceMappings.isEmpty() ? null : sourceMappings.toArray(new SourceMapping[0]);
	}
	
	SourceMapping getSourceMapping(final Source source, final Field field, final Class<?> defaultSourceClass) {
		
		final SourceMapping sourceMapping = new SourceMapping();
		
		// set default source object class
		Optional.ofNullable(defaultSourceClass).ifPresent(sourceMapping::setSourceClass);
		
		// override source property class
		if (!Class.class.equals(source.type())) {
			sourceMapping.setSourceClass(source.type());
		}
		
		if (sourceMapping.getSourceClass() == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
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
			logger.warn("Property {} is not accessible or does not exist in {} and is ignored.", sourceMapping.getPropertyName(), sourceMapping.getSourceClass().getCanonicalName());
			return null;
		}

		// set source object qualifier
		if (StringUtils.isNotBlank(source.qualifier())) {
			sourceMapping.setQualifier(source.qualifier());
		}
		// set conversions to apply
		if (source.map().length > 0) {
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
