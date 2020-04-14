package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.builder.reader.ArraySourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.ArraySourceReader;
import com.apicatalog.projection.property.source.SingleSourceReader;

final class ArraySourceWriterMapper {

	final Logger logger = LoggerFactory.getLogger(ArraySourceWriterMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final ProjectionRegistry registry;
	
	final SingleSourceWriterMapper singleSourceMapper;
	
	public ArraySourceWriterMapper(final ProjectionRegistry registry) {
		this.registry = registry;
		this.singleSourceMapper = new SingleSourceWriterMapper(registry);
	}
		
	Optional<PropertyWriter> getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		final boolean targetReference = PropertyReaderMapper.isReference(targetSetter.getType());
		
		ObjectType sourceTargetType = targetSetter.getType();
		
		if (targetReference) {
			if (targetSetter.getType().isCollection()) {
				sourceTargetType = ObjectType.of(targetSetter.getType().getType(), Object.class);
			} else if (targetSetter.getType().isArray()) {
				sourceTargetType = ObjectType.of(Object[].class);
			} else {
				sourceTargetType = ObjectType.of(Object.class);
			}
		}

		final Optional<ArraySourceReader> arraySourceReader = 
					getArraySourceReader(
							sourcesAnnotation, 
							field.getName(), 
							sourceTargetType,
							defaultSourceClass
							);

		if (arraySourceReader.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(arraySourceReader.get())
					.target(targetSetter, targetReference)
					.build(registry).map(PropertyWriter.class::cast);
	}
	
	Optional<ArraySourceReader> getArraySourceReader(final Sources sourcesAnnotation, final String fieldName, final ObjectType targetType, final Class<?> defaultSourceObjectClass) throws ProjectionError {
		
		ObjectType sourceTargetType = targetType;
		
		if (targetType.isCollection()) {
			sourceTargetType = ObjectType.of(targetType.getComponentType());
			
		} else if (targetType.isArray()) {
			sourceTargetType = ObjectType.of(targetType.getType().getComponentType());
		}

		final Collection<SingleSourceReader> sources = new ArrayList<>(sourcesAnnotation.value().length);
		
		for (final Source source : sourcesAnnotation.value()) {
			singleSourceMapper
				.getSingleSourceReader(source, fieldName, sourceTargetType, defaultSourceObjectClass)
				.ifPresent(sources::add);
		}
		
		if (sources.isEmpty()) {		
			logger.warn(SOURCE_IS_MISSING, fieldName);
			return Optional.empty();
		}

		try {
			return 
				ArraySourceReaderBuilder.newInstance()
					.optional(sourcesAnnotation.optional())
					.sources(sources.toArray(new SingleSourceReader[0]))
					.targetType(targetType)
					.converters(SingleSourceReaderMapper.getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
					.build(registry.getTypeConversions());
			
		} catch (ConverterError | ProjectionError e) {
			logger.error("Property " + fieldName + " is ignored.", e);
			return Optional.empty();
		}
	}
}
