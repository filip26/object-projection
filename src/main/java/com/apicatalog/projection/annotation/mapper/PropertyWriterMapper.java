package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.builder.ComposerBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.ConstantWriterBuilder;
import com.apicatalog.projection.builder.writer.ProvidedPropertyWriterBuilder;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.SourcePropertyWriter;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.property.target.Composer;

final class PropertyWriterMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyWriterMapper.class);
	
	final ProjectionRegistry registry;
	
	final SingleSourceWriterMapper singleSourceMapper;
	final ArraySourceWriterMapper arraySourceMapper;
	
	public PropertyWriterMapper(final ProjectionRegistry registry) {
		this.registry = registry;

		this.singleSourceMapper = new SingleSourceWriterMapper(registry);
		this.arraySourceMapper = new ArraySourceWriterMapper(registry);
	}
	
	public Optional<PropertyWriter> getProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionBuilderError {

		if (logger.isDebugEnabled()) {
			logger.debug("Get property {} : {}, object={}", field.getName(), field.getType().getSimpleName(), defaultSourceClass != null ? defaultSourceClass.getSimpleName() : "n/a");
		}
		
		final Optional<? extends PropertyWriter> mapping;
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = singleSourceMapper.getSourceProperty(field, defaultSourceClass);

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			mapping = arraySourceMapper.getSourcesPropertyMapping(field, defaultSourceClass);

		// provided -> global source
		} else if (field.isAnnotationPresent(Provided.class)) {
			mapping = getProvidedProperty(field);
			
		// constant value
		} else if (field.isAnnotationPresent(Constant.class)) {
			mapping = getConstantProperty(field);

		// direct mapping or a reference
		} else {
			mapping = getDefaultProperty(field, defaultSourceClass);
		}
		
		if (field.isAnnotationPresent(Visibility.class)) {
			mapping.ifPresent(m -> m.setVisibility(
										IntStream.of(field.getAnnotation(Visibility.class).level())
												.boxed()
												.collect(Collectors.toSet())
							));
		}
		
		return mapping.map(PropertyWriter.class::cast);
	}

	Optional<PropertyWriter> getDefaultProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionBuilderError {

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return Optional.empty();
		}

		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		final boolean targetReference = PropertyReaderMapper.isReference(targetSetter.getType());
		
		final Optional<SingleSourceReaderBuilder> sourceReaderBuilder = 
				singleSourceMapper.getSingleSourceReader(
						defaultSourceClass, 
						field.getName(),
						SingleSourceReaderBuilder.newInstance()
							.objectClass(defaultSourceClass)
							.optional(true)
							.targetType(targetSetter.getType(), targetReference)
						);
		
		if (sourceReaderBuilder.isEmpty()) {
			return Optional.empty();
		}

		final Optional<SingleSourceReader> sourceReader = sourceReaderBuilder.get().build(registry.getTypeConversions()); 
		
		if (sourceReader.isEmpty()) {
			logger.warn("Source is missing. Property {} is ignored.", field.getName());
			return Optional.empty();
		}
		
		final Optional<Composer> composer =  
				ComposerBuilder.newInstance()
					.setter(targetSetter, targetReference)
					.build(registry);
	
		return Optional.of(new SourcePropertyWriter(sourceReader.get(), targetSetter, composer.orElse(null)));
	}

	Optional<PropertyWriter> getProvidedProperty(final Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
			
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		return ProvidedPropertyWriterBuilder.newInstance()
					.optional(provided.optional())
					.qualifier(provided.name())
					.targetSetter(targetSetter)
					.targetReference(PropertyReaderMapper.isReference(targetSetter.getType()))
					.build(registry);		
	}				

	Optional<PropertyWriter> getConstantProperty(final Field field) throws ProjectionBuilderError {
		
		final Constant constant = field.getAnnotation(Constant.class);
		
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));
		
		return ConstantWriterBuilder
					.newInstance()
					.constants(constant.value())
					.targetSetter(targetSetter, PropertyReaderMapper.isReference(targetSetter.getType()))
					.build(registry).map(PropertyWriter.class::cast);
	}	
}
