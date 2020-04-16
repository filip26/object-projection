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

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.SingleSourceReader;

final class SingleSourceWriterMapper {

	final Logger logger = LoggerFactory.getLogger(SingleSourceWriterMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final ProjectionRegistry registry;
	
	public SingleSourceWriterMapper(final ProjectionRegistry registry) {
		this.registry = registry;
	}
	
	Optional<PropertyWriter> getSourceProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionBuilderError {

		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));
		
		final boolean targetReference = PropertyReaderMapper.isReference(targetSetter.getType());

		final Optional<SingleSourceReaderBuilder> sourceReaderBuilder = 
				getSingleSourceReader( 
					sourceAnnotation,
					field.getName(),
					defaultSourceClass
					); 

		if (sourceReaderBuilder.isEmpty()) {
			return Optional.empty();
		}
		
		final Optional<SingleSourceReader> sourceReader = 
					sourceReaderBuilder.get()
						.targetType(targetSetter.getType(), targetReference)
						.build(registry.getTypeConversions()); 

		if (sourceReader.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}

		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.target(targetSetter, targetReference)
					.build(registry).map(PropertyWriter.class::cast);
	}

	protected Optional<SingleSourceReaderBuilder> getSingleSourceReader(final Source sourceAnnotation, final String fieldName, final Class<?> defaultSourceClass) {
				
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
				;

		// set conversions to apply
		if (Optional.ofNullable(sourceAnnotation.map()).isPresent()) {
			try {
				sourceBuilder = sourceBuilder.converters(getConverterMapping(sourceAnnotation.map()));
				
			} catch (ConverterError e) {
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
	
	Optional<SingleSourceReaderBuilder> getSingleSourceReader(final Class<?> sourceObjectClass, final String sourceFieldName, final SingleSourceReaderBuilder sourceBuilder) {
		
		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourceFieldName);

		return Optional.ofNullable(sourceBuilder.getter(sourceGetter));
	}

	protected static final Collection<ConverterMapping> getConverterMapping(final Conversion[] conversions) throws ConverterError {

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
