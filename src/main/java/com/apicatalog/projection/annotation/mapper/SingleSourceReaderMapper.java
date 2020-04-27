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

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.SourcePropertyReader;

final class SingleSourceReaderMapper {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReaderMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final Registry registry;
	
	public SingleSourceReaderMapper(final Registry registry) {
		this.registry = registry;
	}
	
	Optional<SourcePropertyReader> getSourceProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		
		final Optional<SingleSourceWriterBuilder> sourceWriterBuilder = 
					getSingleSourceWriter( 
						sourceAnnotation,
						field.getName(),
						defaultSourceClass
						);
		
		if (sourceWriterBuilder.isEmpty()) {
			return Optional.empty();			
		}
		
		final SourcePropertyReaderBuilder sourcePropertyReaderBuilder = 
					SourcePropertyReaderBuilder.newInstance()
						.sourceWriter(sourceWriterBuilder.get())
						.target(targetGetter);

		PropertyReaderMapper.getProjectionName(targetGetter.getType()).ifPresent(sourcePropertyReaderBuilder::targetProjection);

		return sourcePropertyReaderBuilder.build(registry);
	}

	Optional<SingleSourceWriterBuilder> getSingleSourceWriter(final Source sourceAnnotation, final String fieldName, final Class<?> defaultSourceClass) throws ProjectionError {
		
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
		
		final SingleSourceWriterBuilder sourceBuilder = SingleSourceWriterBuilder.newInstance()
				.objectClass(sourceObjectClass)
				.optional(sourceAnnotation.optional())
				.qualifier(sourceAnnotation.name())
				.mode(sourceAnnotation.mode())
				;

		// set conversions to apply
		if (Optional.ofNullable(sourceAnnotation.map()).isPresent()) {
			sourceBuilder.converters(getConverterMapping(sourceAnnotation.map()));				
		}
		
		return Optional.of(getSingleSourceWriter(
					sourceObjectClass, 
					sourceFieldName, 
					sourceBuilder
					));
	}

	SingleSourceWriterBuilder getSingleSourceWriter(final Class<?> sourceObjectClass, final String sourceFieldName, final SingleSourceWriterBuilder sourceBuilder) throws ProjectionError {

		try {
			// extract setter
			return sourceBuilder.setter(ObjectUtils.getSetter(sourceObjectClass, sourceFieldName));
			
		} catch (ObjectError e) {
			throw new ProjectionError("Can not get setter for " + sourceObjectClass.getCanonicalName() + "." + sourceFieldName + ".", e);
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
