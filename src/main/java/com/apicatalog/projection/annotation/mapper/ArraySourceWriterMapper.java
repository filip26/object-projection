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
import com.apicatalog.projection.builder.writer.TargetWriterBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.ArraySourceReader;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.property.target.TargetWriter;

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
		
		final Optional<TargetWriter> targetWriter =  
				TargetWriterBuilder.newInstance()
					.setter(targetSetter, PropertyReaderMapper.isReference(targetSetter.getType()))
					.build(registry);
			
		if (targetWriter.isEmpty()) {
			logger.warn("Target is not readable. Property {} is ignored.", field.getName());
			return Optional.empty();
		}

		final Optional<ArraySourceReader> arraySourceReader = 
					getArraySourceReader(
							sourcesAnnotation, 
							field.getName(), 
							targetWriter.get().getType(),
							defaultSourceClass
							);

		if (arraySourceReader.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(arraySourceReader.get())
					.targetWriter(targetWriter.get())
					.build(registry).map(PropertyWriter.class::cast);
	}
	
	Optional<ArraySourceReader> getArraySourceReader(final Sources sourcesAnnotation, final String fieldName, final ObjectType targetType, final Class<?> defaultSourceObjectClass) throws ProjectionError {
		
		final ObjectType sourceTargetType = (targetType.isCollection()) ? ObjectType.of(targetType.getComponentType())
		
								: ((targetType.isArray()) ?
			ObjectType.of(targetType.getType().getComponentType()) : targetType);

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
