package com.apicatalog.projection.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.apicatalog.projection.objects.access.FieldGetter;
import com.apicatalog.projection.objects.access.FieldSetter;
import com.apicatalog.projection.objects.access.Getter;
import com.apicatalog.projection.objects.access.Setter;
import com.apicatalog.projection.property.ConstantProperty;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.ProvidedObjectProperty;
import com.apicatalog.projection.property.ProvidedProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.source.SingleSource;
import com.apicatalog.projection.target.TargetAdapter;
import com.apicatalog.projection.target.TargetProjectionConverter;
import com.apicatalog.projection.target.TargetTypeConverter;

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
		
		final Getter targetGetter = FieldGetter.from(field);
		final Setter targetSetter = FieldSetter.from(field);

		property.setTargetGetter(targetGetter);
		property.setTargetSetter(targetSetter);

		property.setTargetAdapter(getTargetConverter(
							source.getTargetClass(), 
							source.getTargetComponentClass(), 
							targetSetter.getValueClass(), 
							targetSetter.getValueComponentClass()
						)
				);

		return property;
	}


	ProjectionProperty getProvidedMapping(Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
		
		if (field.getType().isAnnotationPresent(Projection.class)) {
			return getProvidedProjection(provided, field);
		}
		
		final ProvidedObjectProperty property = new ProvidedObjectProperty();
		
		final Getter targetGetter = FieldGetter.from(field);
		final Setter targetSetter = FieldSetter.from(field);
		
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
		
		property.setTargetAdapter(getTargetConverter(null, null, targetSetter.getValueClass(), targetSetter.getValueComponentClass()));

		return property;
	}
	
	ProjectionProperty getProvidedProjection(Provided provided, Field field) {
		
		final ProvidedProjectionProperty property = new ProvidedProjectionProperty(factory);

		// set access mode
		switch (provided.mode()) {
		case READ_ONLY:
			property.setTargetSetter(FieldSetter.from(field));
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(FieldGetter.from(field));
			break;
		
		case READ_WRITE:
			property.setTargetGetter(FieldGetter.from(field));
			property.setTargetSetter(FieldSetter.from(field));
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

		property.setTargetAdapter(getTargetConverter(null, null, field.getType(), targetComponentClass));

		// set target setter
		property.setTargetSetter(FieldSetter.from(field));
		
		return property;
	}

	TargetAdapter getTargetConverter(Class<?> sourceClass, Class<?> sourceComponentClass, Class<?> targetClass, Class<?> targetComponentClass) {

		if (targetClass.isAnnotationPresent(Projection.class)) {
			return new TargetProjectionConverter(factory, sourceClass, sourceComponentClass, targetClass, targetComponentClass);
		}
		
		return new TargetTypeConverter(typeAdapters, sourceClass, sourceComponentClass, targetClass, targetComponentClass);

	}
}
