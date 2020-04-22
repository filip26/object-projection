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
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectError;
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
	
	Optional<PropertyWriter> getSourceProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));
		
		final boolean targetReference = PropertyReaderMapper.isReference(targetSetter.getType());

		final Optional<SingleSourceReader> sourceReader = 
						getSingleSourceReader( 
							sourceAnnotation,
							field.getName(),
							defaultSourceClass
							)
						.targetType(targetSetter.getType(), targetReference)
						.build(registry.getTypeConversions()); 

		if (sourceReader.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(SourcePropertyWriterBuilder.newInstance()
							.sourceReader(sourceReader.get())
							.target(targetSetter, targetReference)
							.build(registry))
						.map(PropertyWriter.class::cast);
	}

	protected SingleSourceReaderBuilder getSingleSourceReader(final Source sourceAnnotation, final String fieldName, final Class<?> defaultSourceClass) throws ProjectionError {
				
		Class<?> sourceObjectClass = defaultSourceClass;
		
		// override source property class
		if (!Class.class.equals(sourceAnnotation.type())) {
			sourceObjectClass = sourceAnnotation.type();
		}
		
		if (sourceObjectClass == null) {
			throw new ProjectionError("Source class is not set for " + fieldName + ". Can not build source reader.");
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
			sourceBuilder = sourceBuilder.converters(getConverterMapping(sourceAnnotation.map()));
		}

		return getSingleSourceReader(
					sourceObjectClass, 
					sourceFieldName, 
					sourceBuilder
					);
	}
	
	SingleSourceReaderBuilder getSingleSourceReader(final Class<?> sourceObjectClass, final String sourceFieldName, final SingleSourceReaderBuilder sourceBuilder) throws ProjectionError {
		
		try {
			// extract getter
			final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourceFieldName);
	
			return sourceBuilder.getter(sourceGetter);
		} catch (ObjectError e) {
			throw new ProjectionError("Can not get getter for " + sourceFieldName + "." + sourceFieldName + ".", e);
		}
	}

	protected static final Collection<ConverterMapping> getConverterMapping(final Conversion[] conversions) throws ProjectionError {

		if (conversions == null || conversions.length == 0) {
			return Collections.emptyList();
		}

		final List<ConverterMapping> converters = new ArrayList<>(conversions.length);
		
		for (final Conversion conversion : conversions) {
			converters.add(
					ConversionMappingBuilder
							.newInstance()
							.converter(conversion.type())
							.parameters(conversion.value())
							.build()
							);
		}

		return converters;
	}	
}
