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
import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.property.source.SingleSourceWriter;

class SingleSourceMapper {

	final Logger logger = LoggerFactory.getLogger(SingleSourceMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final TypeConversions typeConversions;
	final ProjectionRegistry index;
	
	final TypeAdaptersLegacy typeAdapters;
	
	public SingleSourceMapper(ProjectionRegistry index, TypeConversions typeConversions, TypeAdaptersLegacy typeAdapters) {
		this.index = index;
		this.typeConversions = typeConversions;
		this.typeAdapters = typeAdapters;
	}
	
	Optional<SourceProperty> getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final ObjectType targetType =  ObjectUtils.getTypeOf(field);

		final Optional<SingleSourceReader> sourceReader = 
					getSingleSourceReader( 
						sourceAnnotation,
						field.getName(),
						targetType,
						defaultSourceClass
						); 

		final Optional<SingleSourceWriter> sourceWriter = 
				getSingleSourceWriter( 
					sourceAnnotation,
					field.getName(),
					targetType,
					defaultSourceClass
					); 
		
		if (sourceReader.isEmpty() && sourceWriter.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		final SourcePropertyBuilder builder = SourcePropertyBuilder.newInstance();
		
		final Setter targetSetter = FieldSetter.from(field, targetType);
		
		sourceReader.ifPresent(reader -> {
										builder.sourceReader(reader);
										builder.targetSetter(targetSetter);
										});
		
		sourceWriter.ifPresent(writer -> { 
										builder.sourceWriter(writer);
										builder.targetGetter(FieldGetter.from(field, targetType));
										});
		
		return builder
					.mode(sourceAnnotation.mode())
					.targetReference(PropertyMapper.isReference(targetType))						
					.build(index, typeAdapters);
	}

	protected Optional<SingleSourceReader> getSingleSourceReader(Source sourceAnnotation, String fieldName, ObjectType targetType, Class<?> defaultSourceClass) {
				
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
				.targetType(PropertyMapper.getSourceTargetType(targetType))				
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
	
	Optional<SingleSourceWriter> getSingleSourceWriter(Source sourceAnnotation, String fieldName, ObjectType targetType, Class<?> defaultSourceClass) {
		
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
				.targetType(PropertyMapper.getSourceTargetType(targetType))
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
	Optional<SingleSourceReader> getSingleSourceReader(Class<?> sourceObjectClass, String sourceFieldName, SingleSourceReaderBuilder sourceBuilder) {
		
		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourceFieldName);

		return sourceBuilder.getter(sourceGetter).build(typeConversions);
	}

	Optional<SingleSourceWriter> getSingleSourceWriter(Class<?> sourceObjectClass, String sourceFieldName, SingleSourceWriterBuilder sourceBuilder) {
		
		// extract setter
		final Setter sourceSetter = ObjectUtils.getSetter(sourceObjectClass, sourceFieldName);

		return sourceBuilder.setter(sourceSetter).build(typeConversions);
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
