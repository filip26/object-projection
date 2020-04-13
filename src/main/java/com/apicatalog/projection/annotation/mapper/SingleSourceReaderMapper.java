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
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.reader.TargetReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.SourcePropertyReader;
import com.apicatalog.projection.property.source.SingleSourceWriter;
import com.apicatalog.projection.property.target.TargetReader;

class SingleSourceReaderMapper {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReaderMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final ProjectionRegistry registry;
	
	public SingleSourceReaderMapper(ProjectionRegistry registry) {
		this.registry = registry;
	}
	
	Optional<SourcePropertyReader> getSourceProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		
		final Optional<TargetReader> targetReader =  
				TargetReaderBuilder.newInstance()
					.getter(targetGetter, PropertyReaderMapper.isReference(targetGetter.getType()))
					.build(registry);
	
		if (targetReader.isEmpty()) {
			logger.warn("Target is not readable. Property {} is ignored.", field.getName());
			return Optional.empty();
		}

		final Optional<SingleSourceWriter> sourceWriter = 
					getSingleSourceWriter( 
						sourceAnnotation,
						field.getName(),
						targetGetter.getType(),
						defaultSourceClass
						); 
		
		if (sourceWriter.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
				
		return SourcePropertyReaderBuilder.newInstance()
					.sourceWriter(sourceWriter.get())
					.targetReader(targetReader.get())						
					.build(registry);
	}

	Optional<SingleSourceWriter> getSingleSourceWriter(Source sourceAnnotation, String fieldName, ObjectType targetType, Class<?> defaultSourceClass) throws ProjectionError {
		
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
		
		SingleSourceWriterBuilder sourceBuilder = SingleSourceWriterBuilder.newInstance()
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
		
		return getSingleSourceWriter(
					sourceObjectClass, 
					sourceFieldName, 
					sourceBuilder
					);
	}

	Optional<SingleSourceWriter> getSingleSourceWriter(Class<?> sourceObjectClass, String sourceFieldName, SingleSourceWriterBuilder sourceBuilder) throws ProjectionError {
		
		// extract setter
		final Setter sourceSetter = ObjectUtils.getSetter(sourceObjectClass, sourceFieldName);

		return sourceBuilder.setter(sourceSetter).build(registry.getTypeConversions());
	}
	
	protected static final Collection<ConverterMapping> getConverterMapping(Conversion[] conversions) throws ConverterError, ProjectionError {

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