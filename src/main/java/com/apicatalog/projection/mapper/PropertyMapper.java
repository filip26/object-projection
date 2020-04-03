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

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.beans.FieldGetter;
import com.apicatalog.projection.beans.FieldSetter;
import com.apicatalog.projection.beans.Getter;
import com.apicatalog.projection.beans.MethodGetter;
import com.apicatalog.projection.beans.MethodSetter;
import com.apicatalog.projection.beans.Setter;
import com.apicatalog.projection.builder.TargetBuilder;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.property.ConstantProperty;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.ProvidedObjectProperty;
import com.apicatalog.projection.property.ProvidedProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.source.SingleSource;

public class PropertyMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	final SourceMapper sourceMapper;
	
	public PropertyMapper(ProjectionFactory factory, TypeAdapters typeAdapters) {
		this.factory = factory;
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

		final SourceProperty property = new SourceProperty();

		final SingleSource source = sourceMapper.getSingleSource(defaultSourceClass, field.getName(), true, null, AccessMode.READ_WRITE, null);
		
		if (source == null) {
			logger.warn("Source is missing. Property {} is ignored.", field.getName());
			return null;
		}
		
		property.setSource(source);
		
		final Getter targetGetter = FieldGetter.from(field, getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, getTypeOf(field));

		property.setTargetGetter(targetGetter);
		property.setTargetSetter(targetSetter);
		
		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(ObjectType.of(source.getTargetClass(), source.getTargetComponentClass()))
					.target(targetSetter.getType())
					.build(factory, typeAdapters)
					);

		return property;
	}


	ProjectionProperty getProvidedMapping(Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
		
		if (field.getType().isAnnotationPresent(Projection.class)) {
			return getProvidedProjection(provided, field);
		}
		
		final ProvidedObjectProperty property = new ProvidedObjectProperty();
		
		final Getter targetGetter = FieldGetter.from(field, getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, getTypeOf(field));
		
		// set access mode
		switch (provided.mode()) {
		case READ_ONLY:
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setTargetGetter(targetGetter);
			property.setTargetSetter(targetSetter);
			break;
		}			

		// set qualifier
		property.setSourceObjectQualifier(provided.qualifier());

		property.setOptional(provided.optional());

		property.setTargetAdapter(
					TargetBuilder.newInstance()
						.source(targetSetter.getType())
						.target(targetSetter.getType())
						.build(factory, typeAdapters)
						);

		return property;
	}
	
	ProjectionProperty getProvidedProjection(Provided provided, Field field) {
		
		final ProvidedProjectionProperty property = new ProvidedProjectionProperty(factory);

		// set access mode
		switch (provided.mode()) {
		case READ_ONLY:
			property.setTargetSetter(FieldSetter.from(field, getTypeOf(field)));
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(FieldGetter.from(field, getTypeOf(field)));
			break;
		
		case READ_WRITE:
			property.setTargetGetter(FieldGetter.from(field, getTypeOf(field)));
			property.setTargetSetter(FieldSetter.from(field, getTypeOf(field)));
			break;
		}	

		// set qualifier
		property.setSourceObjectQualifier(provided.qualifier());

		return property;
	}

	ProjectionProperty getConstantMapping(Field field) {
		
		final Constant constant = field.getAnnotation(Constant.class);
		
		final ConstantProperty property = new ConstantProperty();
		
		// set constant values
		property.setConstants(constant.value());
		
		Class<?> targetComponentClass = null; 
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			// set target component class
			targetComponentClass = ((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
		}

		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.target(ObjectType.of(field.getType(), targetComponentClass))
					.build(factory, typeAdapters)
					);

		// set target setter
		property.setTargetSetter(FieldSetter.from(field, getTypeOf(field)));
		
		return property;
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
