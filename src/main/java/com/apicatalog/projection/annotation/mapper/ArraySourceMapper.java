package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.builder.ArraySourceReaderBuilder;
import com.apicatalog.projection.builder.ArraySourceWriterBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.property.source.ArraySourceReader;
import com.apicatalog.projection.property.source.ArraySourceWriter;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.property.source.SingleSourceWriter;

class ArraySourceMapper {

	final Logger logger = LoggerFactory.getLogger(ArraySourceMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final TypeConversions typeConversions;
	final ProjectionRegistry index;
	
	final TypeAdaptersLegacy typeAdapters;
	
	final SingleSourceMapper singleSourceMapper;
	
	public ArraySourceMapper(ProjectionRegistry index, TypeConversions typeConversions, TypeAdaptersLegacy typeAdapters) {
		this.index = index;
		this.typeConversions = typeConversions;
		this.typeAdapters = typeAdapters;
		this.singleSourceMapper = new SingleSourceMapper(index, typeConversions, typeAdapters);
	}
		
	Optional<SourceProperty> getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final Optional<ArraySourceReader> arraySourceReader = getArraySourceReader(sourcesAnnotation, field, defaultSourceClass);
		final Optional<ArraySourceWriter> arraySourceWriter = getArraySourceWriter(sourcesAnnotation, field, defaultSourceClass);

		if (arraySourceReader.isEmpty() && arraySourceWriter.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		ObjectType targetType =  ObjectUtils.getTypeOf(field);

		final SourcePropertyBuilder builder = SourcePropertyBuilder.newInstance();
		
		final Setter targetSetter = FieldSetter.from(field, targetType);
		
		arraySourceReader.ifPresent(reader -> {
									builder.sourceReader(reader);
									builder.targetSetter(targetSetter);
								});
		
		arraySourceWriter.ifPresent(writer -> {
									builder.sourceWriter(writer);
									builder.targetGetter(FieldGetter.from(field, targetType));
								});

		return builder
				.mode(AccessMode.READ_WRITE)
				.targetReference(PropertyMapper.isReference(targetSetter.getType()))	//FIXME
				.build(index, typeAdapters);
	}
	
	Optional<ArraySourceReader> getArraySourceReader(final Sources sourcesAnnotation, final Field field, final Class<?> defaultSourceObjectClass) {
		
		SingleSourceReader[] sources = Arrays.stream(sourcesAnnotation.value())
										.map(s -> singleSourceMapper.getSingleSourceReader(s, field, defaultSourceObjectClass))
										.flatMap(Optional::stream)
										.collect(Collectors.toList())
										.toArray(new SingleSourceReader[0])
										;
		
		if (sources.length == 0) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}

		ArraySourceReaderBuilder builder = 
				ArraySourceReaderBuilder.newInstance()
					.optional(sourcesAnnotation.optional())
					.sources(sources)
					;

		try {
			return builder
						.converters(SingleSourceMapper.getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
						.build(typeConversions);
			
		} catch (ConverterError | ProjectionError e) {
			logger.error("Property " + field.getName() + " is ignored.", e);
			return Optional.empty();
		}				
	}	

	Optional<ArraySourceWriter> getArraySourceWriter(final Sources sourcesAnnotation, final Field field, final Class<?> defaultSourceObjectClass) {
		
		SingleSourceWriter[] sources = Arrays.stream(sourcesAnnotation.value())
										.map(s -> singleSourceMapper.getSingleSourceWriter(s, field, defaultSourceObjectClass))
										.flatMap(Optional::stream)
										.collect(Collectors.toList())
										.toArray(new SingleSourceWriter[0])
										;
		
		if (sources.length == 0) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}

		ArraySourceWriterBuilder builder = 
				ArraySourceWriterBuilder.newInstance()
					.optional(sourcesAnnotation.optional())
					.sources(sources)
					;

		try {
			return builder
						.converters(SingleSourceMapper.getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
						.build(typeConversions);
			
		} catch (ConverterError | ProjectionError e) {
			logger.error("Property " + field.getName() + " is ignored.", e);
			return Optional.empty();
		}				
	}	
}
