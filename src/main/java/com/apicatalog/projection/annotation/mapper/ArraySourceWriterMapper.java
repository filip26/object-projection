package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.reader.ArraySourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.ArraySourceReader;

final class ArraySourceWriterMapper {

	final Logger logger = LoggerFactory.getLogger(ArraySourceWriterMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final Registry registry;
	
	final SingleSourceWriterMapper singleSourceMapper;
	
	public ArraySourceWriterMapper(final Registry registry) {
		this.registry = registry;
		this.singleSourceMapper = new SingleSourceWriterMapper(registry);
	}
		
	Optional<PropertyWriter> getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		final Optional<String> targetProjectionName = PropertyReaderMapper.getProjectionName(targetSetter.getType()); 

		final Optional<ArraySourceReader> arraySourceReader = 
					getArraySourceReader(
							sourcesAnnotation, 
							field.getName(), 
							targetSetter.getType(),
							targetProjectionName.orElse(null),
							defaultSourceClass
							);

		if (arraySourceReader.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		final SourcePropertyWriterBuilder builder = 
				SourcePropertyWriterBuilder.newInstance()
					.sourceReader(arraySourceReader.get())
					.target(targetSetter);
		
		targetProjectionName.ifPresent(builder::targetProjection);
		
		return Optional.ofNullable(builder.build(registry)).map(PropertyWriter.class::cast);
	}
	
	Optional<ArraySourceReader> getArraySourceReader(final Sources sourcesAnnotation, final String fieldName, final ObjectType targetType, final String targetProjectionName, final Class<?> defaultSourceObjectClass) throws ProjectionError {
		
		final Collection<SingleSourceReaderBuilder> sources = new ArrayList<>(sourcesAnnotation.value().length);
		
		for (final Source source : sourcesAnnotation.value()) {
			Optional.ofNullable(singleSourceMapper
				.getSingleSourceReader(source, fieldName, defaultSourceObjectClass))
				.ifPresent(sources::add);
		}
		
		if (sources.isEmpty()) {		
			logger.warn(SOURCE_IS_MISSING, fieldName);
			return Optional.empty();
		}

		return
			ArraySourceReaderBuilder.newInstance()
				.optional(sourcesAnnotation.optional())
				.sources(sources)
				.targetType(targetType)
				.targetProjection(targetProjectionName)
				.converters(SingleSourceReaderMapper.getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
				.build(registry.getTypeConversions());
			
	}
}
