package com.apicatalog.projection.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.mapper.mapping.ConversionMappingImpl;
import com.apicatalog.projection.mapper.mapping.ProjectionMappingImpl;
import com.apicatalog.projection.mapper.mapping.PropertyMappingImpl;
import com.apicatalog.projection.mapper.mapping.ProvidedMappingImpl;
import com.apicatalog.projection.mapper.mapping.ReductionMappingImpl;
import com.apicatalog.projection.mapper.mapping.SourceMappingImpl;
import com.apicatalog.projection.mapper.mapping.SourcesMappingImpl;
import com.apicatalog.projection.mapper.mapping.TargetMappingImpl;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.ReductionMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;

public class ProjectionMapper {

	final Logger logger = LoggerFactory.getLogger(ProjectionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory index;
	
	public ProjectionMapper(ProjectionFactory index) {
		this.index = index;
		this.typeAdapters = new TypeAdapters();
	}
	
	public <P> ProjectionMapping<P> getMapping(final Class<P> targetProjectionClass) {

		if (targetProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		
		// ignore unannotated classes
		if (!targetProjectionClass.isAnnotationPresent(Projection.class)) {
			return null;
		}
		
		logger.debug("Scan {}", targetProjectionClass.getCanonicalName());
		
		final Projection projectionAnnotation = targetProjectionClass.getAnnotation(Projection.class);
		
		final ProjectionMappingImpl<P> projectionMapping = new ProjectionMappingImpl<>(targetProjectionClass, typeAdapters);
		
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
			return getProvidedMapping(field);
		}
		
		// direct mapping or a reference
		return getDefaultPropertyMapping(field, defaultSourceClass);
	}
	
	PropertyMapping getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Sources sources = field.getAnnotation(Sources.class);
		
		final Optional<SourceMapping> sourceMappings = 
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

		final PropertyMappingImpl propertyMapping = new PropertyMappingImpl();
		// set projection property name
		propertyMapping.setName(field.getName());

		// set projection property value sources
		sourceMappings.ifPresent(propertyMapping::setSource);
		
		// set property target mapping
		propertyMapping.setTarget(getTargetMapping(field));
		
		return propertyMapping;
	}
	
	PropertyMapping getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Source source = field.getAnnotation(Source.class);
		
		final Optional<SourceMapping> sourceMapping = 
				Optional.ofNullable(
							getSourceMapping(
										source, 
										field,
										defaultSourceClass
									));
		
		if (sourceMapping.isEmpty()) {
			return null;
		}

		final PropertyMappingImpl propertyMapping = new PropertyMappingImpl();
		// set projection property name
		propertyMapping.setName(field.getName());

		// set projection property value sources
		sourceMapping.ifPresent(propertyMapping::setSource);

		// set property target mapping
		propertyMapping.setTarget(getTargetMapping(field));

		return propertyMapping;
	}

	PropertyMapping getDefaultPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;				
		}

		final SourceMappingImpl sourceMapping = new SourceMappingImpl(typeAdapters);
		
		// set default source object class
		sourceMapping.setSourceClass(defaultSourceClass);

		// set default source object property name -> use the same name
		sourceMapping.setPropertyName(field.getName());
		
		sourceMapping.setPropertyType(field.getType());
		
		// check if field exists and is accessible
		if (!isFieldPresent(sourceMapping.getSourceClass(), sourceMapping.getPropertyName())) {
			logger.warn("Property {} is not accessible or does not exist in {} and is ignored.", field.getName(), sourceMapping.getSourceClass().getSimpleName());
			return null;
		}
		
		final PropertyMappingImpl propertyMapping = new PropertyMappingImpl();
		// set projection property name
		propertyMapping.setName(field.getName());
		// set projection property value sources
		propertyMapping.setSource(sourceMapping);
		// set property target mapping 
		propertyMapping.setTarget(getTargetMapping(field));
		
		return propertyMapping;
	}

	PropertyMapping getProvidedMapping(Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
		
		final ProvidedMappingImpl sourceMapping = new ProvidedMappingImpl(index);
		sourceMapping.setOptional(provided.optional());
		
		// set target object class
		sourceMapping.setSourceClass(field.getType());

		sourceMapping.setQualifier(provided.qualifier());
		
		final PropertyMappingImpl propertyMapping = new PropertyMappingImpl();
		// set projection property name		
		propertyMapping.setName(field.getName());
		// set projection property value sources
		propertyMapping.setSource(sourceMapping);		
		// set property target mapping
		propertyMapping.setTarget(getTargetMapping(field));
		
		return propertyMapping;
	}
	
	TargetMapping getTargetMapping(Field field) {
		final TargetMappingImpl targetMapping = new TargetMappingImpl(index);
		// set projection property target class
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
	
	SourceMapping getSourcesMapping(final Sources sources, final Field field, final Class<?> defaultSourceClass) {
		
		final SourcesMappingImpl mapping = new SourcesMappingImpl();
		
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
		
		if (sourceMappings.isEmpty()) {
			logger.warn("Property {} has no source(s) and is ignored.", field.getName());
			return null;
		}
		
		// set reduction to apply
		mapping.setReduction(getReductionMapping(sources.reduce()));

		// set conversions to apply
		mapping.setConversions(getConversionMapping(sources.map()));

		mapping.setSources(sourceMappings);
		
		return mapping;
	}
	
	SourceMapping getSourceMapping(final Source source, final Field field, final Class<?> defaultSourceClass) {
		
		final SourceMappingImpl mapping = new SourceMappingImpl(typeAdapters);
		
		// set default source object class
		Optional.ofNullable(defaultSourceClass).ifPresent(mapping::setSourceClass);
		
		// override source property class
		if (!Class.class.equals(source.type())) {
			mapping.setSourceClass(source.type());
		}
		
		if (mapping.getSourceClass() == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;
		}

		// set default source object property name -> use the same name
		mapping.setPropertyName(field.getName());
		
		// override source property name
		if (StringUtils.isNotBlank(source.value())) {
			mapping.setPropertyName(source.value());
		}
		
		// check is source field does exist
		if (!isFieldPresent(mapping.getSourceClass(), mapping.getPropertyName())) {
			logger.warn("Property {} is not accessible or does not exist in {} and is ignored.", mapping.getPropertyName(), mapping.getSourceClass().getCanonicalName());
			return null;
		}
		
		mapping.setPropertyType(getFieldType(mapping.getSourceClass(), mapping.getPropertyName()));
		

		// set source object qualifier
		if (StringUtils.isNotBlank(source.qualifier())) {
			mapping.setQualifier(source.qualifier());
		}
		// set conversions to apply
		mapping.setConversions(getConversionMapping(source.map()));
		// set optional 
		mapping.setOptional(source.optional());
				
		return mapping;
	}
	
	ConversionMapping[] getConversionMapping(Conversion[] conversions) {

		if (conversions.length == 0) {
			return new ConversionMapping[0];
		}

		return Stream.of(conversions)
				.map(c -> new ConversionMappingImpl(c.type(), c.value()))
				.collect(Collectors.toList())
				.toArray(new ConversionMapping[0])
				;		
	}

	ReductionMapping getReductionMapping(Reduction reduction) {
		return new ReductionMappingImpl(reduction.type(), reduction.value());
	}

	static Class<?> getFieldType(Class<?> clazz, String name)  {
		
		try {
			return clazz.getDeclaredField(name).getType();
			
		} catch (NoSuchFieldException e) {/* ignore */}
		
		return null;

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
