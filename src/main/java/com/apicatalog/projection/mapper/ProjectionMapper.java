package com.apicatalog.projection.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.mapper.mapping.ConstantMappingImpl;
import com.apicatalog.projection.mapper.mapping.ConversionMappingImpl;
import com.apicatalog.projection.mapper.mapping.ProjectionMappingImpl;
import com.apicatalog.projection.mapper.mapping.PropertyMappingImpl;
import com.apicatalog.projection.mapper.mapping.ProvidedMappingImpl;
import com.apicatalog.projection.mapper.mapping.ReductionMappingImpl;
import com.apicatalog.projection.mapper.mapping.SourceMappingImpl;
import com.apicatalog.projection.mapper.mapping.SourcesMappingImpl;
import com.apicatalog.projection.mapper.mapping.TargetMappingImpl;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.ConverterMapping;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.ReducerMapping;
import com.apicatalog.projection.mapping.ReductionMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;
import com.apicatalog.projection.objects.FieldValueGetter;
import com.apicatalog.projection.objects.FieldValueSetter;
import com.apicatalog.projection.objects.MethodValueGetter;
import com.apicatalog.projection.objects.MethodValueSetter;

public class ProjectionMapper {

	final Logger logger = LoggerFactory.getLogger(ProjectionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	public ProjectionMapper(ProjectionFactory index) {
		this.factory = index;
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
		
		final ProjectionMappingImpl<P> projectionMapping = new ProjectionMappingImpl<>(targetProjectionClass);
		
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

		final Optional<PropertyMappingImpl> mapping;
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = Optional.ofNullable(getSourcePropertyMapping(field, defaultSourceClass));

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			mapping = Optional.ofNullable(getSourcesPropertyMapping(field, defaultSourceClass));

		// provided -> global source
		} else if (field.isAnnotationPresent(Provided.class)) {
			mapping = Optional.ofNullable(getProvidedMapping(field));
			
		// constant value
		} else if (field.isAnnotationPresent(Constant.class)) {
			mapping = Optional.ofNullable(getConstantMapping(field));

		// direct mapping or a reference
		} else {
			mapping = Optional.ofNullable(getDefaultPropertyMapping(field, defaultSourceClass));
		}
		
		if (field.isAnnotationPresent(Visibility.class)) {
			mapping.ifPresent(m -> m.setVisible(
										IntStream.of(field.getAnnotation(Visibility.class).level())
												.boxed()
												.collect(Collectors.toSet())
							));
		}
		
		// set property target mapping
		mapping.ifPresent(m -> m.setTarget(getTargetMapping(field, m.getSource())));
		
		return mapping.orElse(null);
	}
	
	PropertyMappingImpl getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
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
		
		return propertyMapping;
	}
	
	PropertyMappingImpl getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
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

		return propertyMapping;
	}

	PropertyMappingImpl getDefaultPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;				
		}

		final SourceMappingImpl sourceMapping = new SourceMappingImpl(typeAdapters);
		
		// set default source object class
		sourceMapping.setSourceObjectClass(defaultSourceClass);
		
		if (!setGetterSetter(sourceMapping, field.getName())) {
			return null;
		}
				
		sourceMapping.setTargetClass(field.getType());
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			sourceMapping.setTargetComponentClass((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
		}
		
		return (new PropertyMappingImpl())
						.setName(field.getName())		// set projection property name
						.setSource(sourceMapping)		// set projection property value sources
						;
	}

	PropertyMappingImpl getProvidedMapping(Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
		
		final ProvidedMappingImpl sourceMapping = new ProvidedMappingImpl(factory, typeAdapters);
		
		sourceMapping.setOptional(provided.optional());
		
		sourceMapping.setReference(field.getType().isAnnotationPresent(Projection.class));

		sourceMapping.setTargetClass(field.getType());
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			sourceMapping.setTargetComponentClass((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
		}
		
		sourceMapping.setQualifier(provided.qualifier());
		
		return (new PropertyMappingImpl())
				.setName(field.getName())		// set projection property name
				.setSource(sourceMapping)		// set projection property value sources
				;
	}

	PropertyMappingImpl getConstantMapping(Field field) {
		
		final Constant constant = field.getAnnotation(Constant.class);
		
		final ConstantMappingImpl sourceMapping = new ConstantMappingImpl(typeAdapters);
		
		// set constant values
		sourceMapping.setConstants(constant.value());

		// set target object class
		sourceMapping.setTargetClass(field.getType());
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			// set target component class
			sourceMapping.setTargetComponentClass((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
		}

		return (new PropertyMappingImpl())
				.setName(field.getName())		// set projection property name
				.setSource(sourceMapping)		// set projection property value sources
				;
	}

	TargetMapping getTargetMapping(Field field, SourceMapping sourceMapping) {
		final TargetMappingImpl targetMapping = new TargetMappingImpl(factory, typeAdapters);
		// set projection property target class
		targetMapping.setTargetClass(field.getType());

		// a collection?
		if (Collection.class.isAssignableFrom(field.getType())) {
			targetMapping.setTargetComponentClass((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
			targetMapping.setReference(targetMapping.getTargetComponentClass().isAnnotationPresent(Projection.class));
			
		} else {
			targetMapping.setReference(targetMapping.getTargetClass().isAnnotationPresent(Projection.class));
		}
		
		// source's output is target's input
		targetMapping.setSourceClass(sourceMapping.getTargetClass());
		targetMapping.setSourceComponentClass(sourceMapping.getTargetComponentClass());
		
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
		
		mapping.setTargetClass(mapping.getReduction().getReducerMapping().getTargetClass());
		mapping.setTargetComponentClass(mapping.getReduction().getReducerMapping().getTargetComponentClass());

		// set conversions to apply
		mapping.setConversions(getConversionMapping(sources.map()));

		mapping.setSources(sourceMappings);
		
		if (mapping.getConversions() != null) {
			
			Stream.of(mapping.getConversions())
					.reduce((first, second) -> second)
					.map(ConversionMapping::getConverterMapping)
					.ifPresent(m -> { 
								mapping.setTargetClass(m.getSourceClass());
								mapping.setTargetComponentClass(m.getSourceComponentClass());
								});
		}
		
		return mapping;
	}
	
	SourceMapping getSourceMapping(final Source source, final Field field, final Class<?> defaultSourceClass) {
		
		final SourceMappingImpl mapping = new SourceMappingImpl(typeAdapters);
		
		// set default source object class
		Optional.ofNullable(defaultSourceClass).ifPresent(mapping::setSourceObjectClass);
		
		// override source property class
		if (!Class.class.equals(source.type())) {
			mapping.setSourceObjectClass(source.type());
		}
		
		if (mapping.getSourceObjectClass() == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;
		}

		// set default source object property name -> use the same name
		String sourceFieldName = field.getName();
				
		// override source property name
		if (StringUtils.isNotBlank(source.value())) {
			sourceFieldName = source.value();
		}

		if (!setGetterSetter(mapping, sourceFieldName)) {
			return null;
		}
				
//		Field sourceField = ObjectUtils.getProperty(mapping.getSourceObjectClass(), mapping.getPropertyName());
//
//		// check is source field does exist
//		if (sourceField == null) {
//			logger.warn("Property {} is not accessible or does not exist in {} and is ignored.", mapping.getPropertyName(), mapping.getSourceObjectClass().getCanonicalName());
//			return null;
//		}
//				
//		mapping.setSourceClass(sourceField.getType());
//		
//		if (Collection.class.isAssignableFrom(sourceField.getType())) {
//			mapping.setSourceComponentClass((Class<?>)((ParameterizedType) sourceField.getGenericType()).getActualTypeArguments()[0]);
//		}
		
		// set source object qualifier
		if (StringUtils.isNotBlank(source.qualifier())) {
			mapping.setQualifier(source.qualifier());
		}
		// set conversions to apply
		mapping.setConversions(getConversionMapping(source.map()));
		// set optional 
		mapping.setOptional(source.optional());

		mapping.setTargetClass(mapping.getGetter().getValueClass());
		mapping.setTargetComponentClass(mapping.getGetter().getValueComponentClass());
		
		if (mapping.getConversions() != null) {
			
			Stream.of(mapping.getConversions())
					.reduce((first, second) -> second)
					.map(ConversionMapping::getConverterMapping)
					.ifPresent(m -> { 
								mapping.setTargetClass(m.getSourceClass());
								mapping.setTargetComponentClass(m.getSourceComponentClass());
								});
		}

		return mapping;
	}
		
	ConversionMapping[] getConversionMapping(Conversion[] conversions) {

		if (conversions.length == 0) {
			return new ConversionMapping[0];
		}

		return Stream.of(conversions)
				.map(this::getConverterMapping)
				.collect(Collectors.toList())
				.toArray(new ConversionMapping[0])
				;		
	}
	
	ConversionMapping getConverterMapping(final Conversion conversion) {
		
		//FIXME use ConverterFactory
		ConverterMapping converter = new ConverterMapping();
		converter.setConverterClass(conversion.type());
		
		
		Type sourceType = ((ParameterizedType) conversion.type().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		
		Class<?> sourceClass = null;
		Class<?> sourceComponentClass = null;
		
		if (ParameterizedType.class.isInstance(sourceType)) {
			sourceClass = (Class<?>)((ParameterizedType)sourceType).getRawType();
			sourceComponentClass = (Class<?>)((ParameterizedType)sourceType).getActualTypeArguments()[0];
			
		} else {
			sourceClass = (Class<?>) sourceType;
		}
		

		converter.setSourceClass(sourceClass);
		converter.setSourceComponentClass(sourceComponentClass);

		Type targetType = ((ParameterizedType) conversion.type().getGenericInterfaces()[0]).getActualTypeArguments()[1];
		
		Class<?> targetClass = null;
		Class<?> targetComponentClass = null;
		
		if (ParameterizedType.class.isInstance(targetType)) {
			targetClass = (Class<?>)((ParameterizedType)targetType).getRawType();
			targetComponentClass = (Class<?>)((ParameterizedType)targetType).getActualTypeArguments()[0];
			
		} else {
			targetClass = (Class<?>) targetType;
		}

		converter.setTargetClass(targetClass);
		converter.setTargetComponentClass(targetComponentClass);
		
		return new ConversionMappingImpl(converter, typeAdapters, conversion.value());
	}

	ReductionMapping getReductionMapping(Reduction reduction) {
		
		//FIXME use ConverterFactory
		ReducerMapping reducer = new ReducerMapping();

		reducer.setReducerClass(reduction.type());
		//FIXME checks
		
		Class<?> sourceClass = (Class<?>)(((ParameterizedType) reduction.type().getGenericInterfaces()[0]).getActualTypeArguments()[0]); 
		
		// get an array of it
		sourceClass = Array.newInstance(sourceClass, 0).getClass();
		
		reducer.setSourceClass(sourceClass);
		reducer.setTargetClass((Class<?>)(((ParameterizedType) reduction.type().getGenericInterfaces()[0]).getActualTypeArguments()[1]));

		
		return new ReductionMappingImpl(reducer, typeAdapters, reduction.value());
	}
	
	boolean setGetterSetter(final SourceMappingImpl sourceMapping, final String name) {

		final Field sourceField = ObjectUtils.getProperty(sourceMapping.getSourceObjectClass(), name);
		if (sourceField != null) {

			final FieldValueGetter getter = new FieldValueGetter();
			getter.setFieldName(sourceField.getName());
			
			//FIXME do this during creation
			getter.setValueClass(sourceField.getType());

			if (Collection.class.isAssignableFrom(sourceField.getType())) {
				getter.setValueComponentClass((Class<?>)((ParameterizedType) sourceField.getGenericType()).getActualTypeArguments()[0]);
			}

			sourceMapping.setGetter(getter);
			
			final FieldValueSetter setter = new FieldValueSetter(typeAdapters);
			setter.setFieldName(getter.getFieldName());
			//FIXME do this during creation
			setter.setValueClass(getter.getValueClass());
			setter.setValueComponentClass(getter.getValueComponentClass());
			
			sourceMapping.setSetter(setter);
			
			return true;
		}
		
		// look for getter
		final String getterName = "get".concat(StringUtils.capitalize(name));
		
		final Method getterMethod = ObjectUtils.getMethod(sourceMapping.getSourceObjectClass(), getterName);
		
		if (getterMethod != null) {
			final MethodValueGetter getter = new MethodValueGetter(getterMethod);
			
			//FIXME do this during creation
			getter.setValueClass(getterMethod.getReturnType());

			if (Collection.class.isAssignableFrom(getterMethod.getReturnType())) {
				getter.setValueComponentClass((Class<?>)((ParameterizedType) getterMethod.getGenericReturnType()).getActualTypeArguments()[0]);
			}
			
			sourceMapping.setGetter(getter);
		}
		
		final String setterName = "set".concat(StringUtils.capitalize(name));

		final Method setterMethod = ObjectUtils.getMethod(sourceMapping.getSourceObjectClass(), setterName);
		
		if (setterMethod != null) {
			final MethodValueSetter setter = new MethodValueSetter(typeAdapters, setterMethod);
			
			//FIXME do this during creation
			setter.setValueClass(setterMethod.getReturnType());

			if (Collection.class.isAssignableFrom(getterMethod.getReturnType())) {
				setter.setValueComponentClass((Class<?>)((ParameterizedType) setterMethod.getGenericReturnType()).getActualTypeArguments()[0]);
			}
			
			sourceMapping.setSetter(setter);			
		}
		

		if (setterMethod == null && getterMethod == null) {
			logger.warn("Property {} is not accessible or does not exist in {} and is ignored.", name, sourceMapping.getSourceObjectClass().getSimpleName());
			return false;
		}
		
		return true;
	}
}
