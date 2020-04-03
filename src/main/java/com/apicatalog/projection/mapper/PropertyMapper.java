package com.apicatalog.projection.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.builder.ConstantPropertyBuilder;
import com.apicatalog.projection.builder.ProvidedPropertyBuilder;
import com.apicatalog.projection.builder.SingleSourceBuilder;
import com.apicatalog.projection.builder.TargetBuilder;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.getter.FieldGetter;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.getter.MethodGetter;
import com.apicatalog.projection.objects.setter.FieldSetter;
import com.apicatalog.projection.objects.setter.MethodSetter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.source.SingleSource;

public class PropertyMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionRegistry registry;
	
	final SourceMapper sourceMapper;
	
	public PropertyMapper(ProjectionRegistry factory, TypeAdapters typeAdapters) {
		this.registry = factory;
		this.typeAdapters = typeAdapters;
		this.sourceMapper = new SourceMapper(factory, typeAdapters);
	}
	
	ProjectionProperty getPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		final Optional<ProjectionProperty> mapping;
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = Optional.ofNullable(sourceMapper.getSourcePropertyMapping(field, defaultSourceClass));

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			mapping = Optional.ofNullable(sourceMapper.getSourcesPropertyMapping(field, defaultSourceClass));

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
		
		return mapping.orElse(null);
	}	

	ProjectionProperty getDefaultPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;				
		}

		final SingleSourceBuilder sourceBuilder = SingleSourceBuilder.newInstance()
				.objectClass(defaultSourceClass)
				.optional(true);
		
		final SingleSource source = sourceMapper.getSingleSource(defaultSourceClass, field.getName(), sourceBuilder);
		
		if (source == null) {
			logger.warn("Source is missing. Property {} is ignored.", field.getName());
			return null;
		}
		
		final SourceProperty property = new SourceProperty();
		
		property.setSource(source);
		
		final Getter targetGetter = FieldGetter.from(field, getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, getTypeOf(field));

		property.setTargetGetter(targetGetter);
		property.setTargetSetter(targetSetter);
		
		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(source.getTargetType())
					.target(targetSetter.getType())
					.build(registry, typeAdapters)
					);

		return property;
	}

	ProjectionProperty getProvidedMapping(Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
			
		final Getter targetGetter = FieldGetter.from(field, getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, getTypeOf(field));

		return ProvidedPropertyBuilder
					.newInstance()
					.mode(provided.mode())
					.optional(provided.optional())
					.qualifier(provided.qualifier())
					.targetGetter(targetGetter)
					.targetSetter(targetSetter)
					.build(registry, typeAdapters);
	}				

	ProjectionProperty getConstantMapping(Field field) {
		
		final Constant constant = field.getAnnotation(Constant.class);
		
		return ConstantPropertyBuilder
					.newInstance()
					.constants(constant.value())
					.targetSetter(FieldSetter.from(field, getTypeOf(field)))
					.build(registry, typeAdapters);
	}
	
	protected static final ObjectType getTypeOf(Field field) {
		
		Class<?> objectClass = field.getType();
		Class<?> componentClass = null;
		boolean reference = objectClass.isAnnotationPresent(Projection.class);
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			componentClass = (Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			reference = componentClass.isAnnotationPresent(Projection.class);
		}
		
		return ObjectType.of(objectClass, componentClass, reference);
	}

	protected static final ObjectType getTypeOf(Method method) {
		
		Class<?> objectClass = method.getReturnType();
		Class<?> componentClass = null;
		boolean reference = objectClass.isAnnotationPresent(Projection.class);

		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			componentClass = (Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
			reference = componentClass.isAnnotationPresent(Projection.class);
		}
		
		return ObjectType.of(objectClass, componentClass, reference);
	}

	protected static final Getter getGetter(Class<?> objectClass, final String name) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldGetter.from(sourceField, getTypeOf(sourceField));
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(objectClass, "get".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return MethodGetter.from(sourceGetter, name, getTypeOf(sourceGetter));
		}

		return null;
	}
	
	protected static final Setter getSetter(Class<?> objectClass, final String name) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldSetter.from(sourceField, getTypeOf(sourceField));
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(objectClass, "set".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return MethodSetter.from(sourceGetter, name, getTypeOf(sourceGetter));
		}

		return null;
	}
}
