package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.apicatalog.projection.objects.setter.FieldSetter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.property.source.SingleSource;

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
	
	Optional<ProjectionProperty> getPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		final Optional<? extends ProjectionProperty> mapping;
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = sourceMapper.getSourcePropertyMapping(field, defaultSourceClass);

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			mapping = sourceMapper.getSourcesPropertyMapping(field, defaultSourceClass);

		// provided -> global source
		} else if (field.isAnnotationPresent(Provided.class)) {
			mapping = getProvidedMapping(field);
			
		// constant value
		} else if (field.isAnnotationPresent(Constant.class)) {
			mapping = getConstantMapping(field);

		// direct mapping or a reference
		} else {
			mapping = getDefaultPropertyMapping(field, defaultSourceClass);
		}
		
		if (field.isAnnotationPresent(Visibility.class)) {
			mapping.ifPresent(m -> m.setVisibility(
										IntStream.of(field.getAnnotation(Visibility.class).level())
												.boxed()
												.collect(Collectors.toSet())
							));
		}
		
		return mapping.map(ProjectionProperty.class::cast);
	}

	Optional<ProjectionProperty> getDefaultPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return Optional.empty();				
		}

		final SingleSourceBuilder sourceBuilder = SingleSourceBuilder.newInstance()
				.objectClass(defaultSourceClass)
				.optional(true);
		
		final Optional<SingleSource> source = sourceMapper.getSingleSource(defaultSourceClass, field.getName(), sourceBuilder);
		
		if (source.isEmpty()) {
			logger.warn("Source is missing. Property {} is ignored.", field.getName());
			return Optional.empty();
		}
		
		final SourceProperty property = new SourceProperty();
		
		property.setSource(source.get());
		
		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		property.setTargetGetter(targetGetter);
		property.setTargetSetter(targetSetter);
		
		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(source.get().getTargetType())
					.target(targetSetter.getType(), isReference(targetSetter.getType()))
					.build(registry, typeAdapters)
					);

		return Optional.of(property);
	}

	Optional<ProjectionProperty> getProvidedMapping(Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
			
		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		return ProvidedPropertyBuilder
					.newInstance()
					.mode(provided.mode())
					.optional(provided.optional())
					.qualifier(provided.name())
					.targetGetter(targetGetter)
					.targetSetter(targetSetter)
					.targetReference(isReference(targetSetter.getType()))
					.build(registry, typeAdapters);
	}				

	Optional<ProjectionProperty> getConstantMapping(Field field) {
		
		final Constant constant = field.getAnnotation(Constant.class);
		
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));
		
		return ConstantPropertyBuilder
					.newInstance()
					.constants(constant.value())
					.targetSetter(targetSetter, isReference(targetSetter.getType()))
					.build(registry, typeAdapters);
	}
	
	protected static final boolean isReference(ObjectType objectType) {
		return objectType.isCollection()
				? objectType.getComponentClass().isAnnotationPresent(Projection.class)
				: objectType.getType().isAnnotationPresent(Projection.class)
				;		
	}
	
}
