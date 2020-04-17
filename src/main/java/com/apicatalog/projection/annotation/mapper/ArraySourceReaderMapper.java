package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.ArraySourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.PropertyReader;

final class ArraySourceReaderMapper {

	final Logger logger = LoggerFactory.getLogger(ArraySourceReaderMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final ProjectionRegistry registry;
	
	final SingleSourceReaderMapper singleSourceMapper;
	
	public ArraySourceReaderMapper(final ProjectionRegistry registry) {
		this.registry = registry;
		this.singleSourceMapper = new SingleSourceReaderMapper(registry);
	}
		
	Optional<PropertyReader> getSourcesProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionBuilderError {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		
		final boolean targetReference = PropertyReaderMapper.isReference(targetGetter.getType());

		final Optional<ArraySourceWriterBuilder> arraySourceWriterBuilder = 
					getArraySourceWriter(
							sourcesAnnotation, 
							field.getName(),
							defaultSourceClass
							);

		if (arraySourceWriterBuilder.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		return SourcePropertyReaderBuilder.newInstance()
				.sourceWriter(arraySourceWriterBuilder.get())
				.target(targetGetter, targetReference)
				.build(registry).map(PropertyReader.class::cast);
	}
	
	Optional<ArraySourceWriterBuilder> getArraySourceWriter(final Sources sourcesAnnotation, final String fieldName, final Class<?> defaultSourceObjectClass) throws ProjectionBuilderError {
							 								
		final Collection<SingleSourceWriterBuilder> sources = new ArrayList<>(sourcesAnnotation.value().length);

		for (final Source source : sourcesAnnotation.value()) {
			singleSourceMapper
				.getSingleSourceWriter(source, fieldName, defaultSourceObjectClass)
				.ifPresent(sources::add);
		}
		
		if (sources.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, fieldName);
			return Optional.empty();
		}

		try {
			return Optional.of(ArraySourceWriterBuilder.newInstance()
						.optional(sourcesAnnotation.optional())
						.sources(sources)
						.converters(SingleSourceReaderMapper.getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
						);
			
		} catch (ConverterError e) {
			throw new ProjectionBuilderError(e);
		}
	}	
}
