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
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.reader.TargetReaderBuilder;
import com.apicatalog.projection.builder.writer.ArraySourceWriterBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.source.ArraySourceWriter;
import com.apicatalog.projection.property.source.SingleSourceWriter;
import com.apicatalog.projection.property.target.TargetReader;

final class ArraySourceReaderMapper {

	final Logger logger = LoggerFactory.getLogger(ArraySourceReaderMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final ProjectionRegistry registry;
	
	final SingleSourceReaderMapper singleSourceMapper;
	
	public ArraySourceReaderMapper(ProjectionRegistry registry) {
		this.registry = registry;
		this.singleSourceMapper = new SingleSourceReaderMapper(registry);
	}
		
	Optional<PropertyReader> getSourcesProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		
		final Optional<TargetReader> targetReader =  
				TargetReaderBuilder.newInstance()
					.getter(targetGetter, PropertyReaderMapper.isReference(targetGetter.getType()))
					.build(registry);
	
		if (targetReader.isEmpty()) {
			logger.warn("Target is not readable. Property {} is ignored.", field.getName());
			return Optional.empty();
		}

		final Optional<ArraySourceWriter> arraySourceWriter = 
					getArraySourceWriter(
							sourcesAnnotation, 
							field.getName(),
							targetReader.get().getType(),
							defaultSourceClass
							);

		if (arraySourceWriter.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		return SourcePropertyReaderBuilder.newInstance()
				.sourceWriter(arraySourceWriter.get())
				.targetReader(targetReader.get())
				.build(registry).map(PropertyReader.class::cast);
	}
	
	Optional<ArraySourceWriter> getArraySourceWriter(final Sources sourcesAnnotation, final String fieldName, final ObjectType targetType, final Class<?> defaultSourceObjectClass) throws ProjectionError {

		final ObjectType sourceTargetType = 
							(targetType.isCollection()) 
								? ObjectType.of(targetType.getComponentType())
								: ((targetType.isArray()
										? ObjectType.of(targetType.getType().getComponentType()) 
										: targetType));

		final Collection<SingleSourceWriter> sources = new ArrayList<>(sourcesAnnotation.value().length);
		
		for (final Source source : sourcesAnnotation.value()) {
			singleSourceMapper
				.getSingleSourceWriter(source, fieldName, sourceTargetType, defaultSourceObjectClass)
				.ifPresent(sources::add);
		}
		
		if (sources.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, fieldName);
			return Optional.empty();
		}

		try {
			return ArraySourceWriterBuilder.newInstance()
						.optional(sourcesAnnotation.optional())
						.sources(sources.toArray(new SingleSourceWriter[0]))
						.targetType(targetType)
						.converters(SingleSourceReaderMapper.getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
						.build(registry.getTypeConversions());
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}	
}