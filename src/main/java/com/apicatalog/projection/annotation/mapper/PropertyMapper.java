package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.builder.ConstantPropertyBuilder;
import com.apicatalog.projection.builder.ProvidedPropertyBuilder;
import com.apicatalog.projection.builder.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.TargetBuilder;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.property.source.SingleSourceWriter;

public class PropertyMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyMapper.class);
	
	final TypeAdaptersLegacy typeAdapters;
	final TypeConversions typeConversions;
	final ProjectionRegistry registry;
	
	final SingleSourceMapper singleSourceMapper;
	final ArraySourceMapper arraySourceMapper;
	
	public PropertyMapper(ProjectionRegistry factory, TypeConversions typeConversions, TypeAdaptersLegacy typeAdapters) {
		this.registry = factory;
		this.typeConversions = typeConversions;
		this.typeAdapters = typeAdapters;
		this.singleSourceMapper = new SingleSourceMapper(factory, typeConversions, typeAdapters);
		this.arraySourceMapper = new ArraySourceMapper(factory, typeConversions, typeAdapters);
	}
	
	Optional<ProjectionProperty> getPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		final Optional<? extends ProjectionProperty> mapping;
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = singleSourceMapper.getSourcePropertyMapping(field, defaultSourceClass);

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			mapping = arraySourceMapper.getSourcesPropertyMapping(field, defaultSourceClass);

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
		
		final ObjectType targetType =  ObjectUtils.getTypeOf(field);
		
		final Optional<SingleSourceReader> sourceReader = 
				singleSourceMapper.getSingleSourceReader(
						defaultSourceClass, 
						field.getName(),
						SingleSourceReaderBuilder.newInstance()
							.objectClass(defaultSourceClass)
							.optional(true)
							.targetType(PropertyMapper.getSourceTargetType(targetType))
						);
		
		
		final Optional<SingleSourceWriter> sourceWriter = 
				singleSourceMapper.getSingleSourceWriter(
						defaultSourceClass, 
						field.getName(),
						SingleSourceWriterBuilder.newInstance()
							.objectClass(defaultSourceClass)
							.optional(true)						
							.targetType(PropertyMapper.getSourceTargetType(targetType))
						);
		
		if (sourceReader.isEmpty() && sourceWriter.isEmpty()) {
			logger.warn("Source is missing. Property {} is ignored.", field.getName());
			return Optional.empty();
		}
		
		final SourceProperty property = new SourceProperty();
	
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));
	
		sourceReader.ifPresent(reader -> {
								property.setSourceReader(reader);
								property.setTargetSetter(targetSetter);								
								});
		
		sourceWriter.ifPresent(writer -> {
								property.setSourceWriter(writer);
								property.setTargetGetter(FieldGetter.from(field, ObjectUtils.getTypeOf(field)));								
								});

		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(sourceReader.get().getTargetType())	//FIXME !!!!
					.target(targetSetter.getType(), isReference(targetSetter.getType()))	//FIXME
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
				? objectType.getComponentType().isAnnotationPresent(Projection.class)
				: objectType.getType().isAnnotationPresent(Projection.class)
				;		
	}
	
	protected static final ObjectType getSourceTargetType(ObjectType targetType) {
		
		if (targetType.isCollection() && targetType.getComponentType().isAnnotationPresent(Projection.class)) {
			return ObjectType.of(targetType.getType(), Object.class);
		}
		if (targetType.isArray() && targetType.getType().getComponentType().isAnnotationPresent(Projection.class)) {
			return ObjectType.of(Object[].class);
		}
		if (targetType.getType().isAnnotationPresent(Projection.class)) {
			return ObjectType.of(Object.class);
		}
		return targetType;
	}
	
}
