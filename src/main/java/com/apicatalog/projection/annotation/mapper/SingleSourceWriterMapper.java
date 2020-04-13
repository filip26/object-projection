package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.builder.writer.TargetWriterBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.property.target.TargetWriter;

final class SingleSourceWriterMapper {

	final Logger logger = LoggerFactory.getLogger(SingleSourceWriterMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final ProjectionRegistry registry;
	
	public SingleSourceWriterMapper(final ProjectionRegistry registry) {
		this.registry = registry;
	}
	
	Optional<PropertyWriter> getSourceProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));
		
		final Optional<TargetWriter> targetWriter =  
				TargetWriterBuilder.newInstance()
					.setter(targetSetter, PropertyReaderMapper.isReference(targetSetter.getType()))
					.build(registry);
	
		if (targetWriter.isEmpty()) {
			logger.warn("Target is not readable. Property {} is ignored.", field.getName());
			return Optional.empty();
		}

		final Optional<SingleSourceReader> sourceReader = 
					getSingleSourceReader( 
						sourceAnnotation,
						field.getName(),
						targetWriter.get().getType(),
						defaultSourceClass
						); 

		if (sourceReader.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}

		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.targetWriter(targetWriter.get())
					.build(registry).map(PropertyWriter.class::cast);
	}

	protected Optional<SingleSourceReader> getSingleSourceReader(final Source sourceAnnotation, final String fieldName, final ObjectType targetType, final Class<?> defaultSourceClass) throws ProjectionError {
				
		Class<?> sourceObjectClass = defaultSourceClass;
		
		// override source property class
		if (!Class.class.equals(sourceAnnotation.type())) {
			sourceObjectClass = sourceAnnotation.type();
		}
		
		if (sourceObjectClass == null) {
			logger.warn(SOURCE_IS_MISSING, fieldName);
			return Optional.empty();
		}

		// set default source object property name -> use the same name
		String sourceFieldName = fieldName;
				
		// override source property name
		if (StringUtils.isNotBlank(sourceAnnotation.value())) {
			sourceFieldName = sourceAnnotation.value();
		}
		
		SingleSourceReaderBuilder sourceBuilder = SingleSourceReaderBuilder.newInstance()
				.objectClass(sourceObjectClass)
				.optional(sourceAnnotation.optional())
				.qualifier(sourceAnnotation.name())
				.mode(sourceAnnotation.mode())
				.targetType(targetType)				
				;

		// set conversions to apply
		if (Optional.ofNullable(sourceAnnotation.map()).isPresent()) {
			try {
				sourceBuilder = sourceBuilder.converters(getConverterMapping(sourceAnnotation.map()));
				
			} catch (ConverterError | ProjectionError e) {
				logger.error("Property " + sourceFieldName + " is ignored.", e);
				return Optional.empty();
			}
		}

		return getSingleSourceReader(
					sourceObjectClass, 
					sourceFieldName, 
					sourceBuilder
					);
	}
	
	Optional<SingleSourceReader> getSingleSourceReader(final Class<?> sourceObjectClass, final String sourceFieldName, final SingleSourceReaderBuilder sourceBuilder) throws ProjectionError {
		
		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourceFieldName);

		return sourceBuilder.getter(sourceGetter).build(registry.getTypeConversions());
	}
	
	protected static final Collection<ConverterMapping> getConverterMapping(final Conversion[] conversions) throws ConverterError, ProjectionError {

		final List<ConverterMapping> converters = new ArrayList<>(conversions.length);
		
		if (conversions.length == 0) {
			return Collections.emptyList();
		}

		for (final Conversion conversion : conversions) {
			converters.add(
					ConversionMappingBuilder
							.newInstance()
							.converter(conversion.type())
							.parameters(conversion.value())
							.build()
							);
		}

		return converters.isEmpty() ? null : converters;
	}	
}
