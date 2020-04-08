package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.builder.ArraySourceBuilder;
import com.apicatalog.projection.builder.ConversionBuilder;
import com.apicatalog.projection.builder.SingleSourceBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.getter.FieldGetter;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.FieldSetter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.property.source.ArraySource;
import com.apicatalog.projection.property.source.SingleSource;
import com.apicatalog.projection.type.adapter.TypeAdapters;

class SourceMapper {

	final Logger logger = LoggerFactory.getLogger(SourceMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final TypeAdapters typeAdapters;
	final ProjectionRegistry factory;
	
	public SourceMapper(ProjectionRegistry index, TypeAdapters typeAdapters) {
		this.factory = index;
		this.typeAdapters = typeAdapters;
	}
		
	Optional<SourceProperty> getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final Optional<ArraySource> arraySource = getArraySource(sourcesAnnotation, field, defaultSourceClass);

		if (arraySource.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		ObjectType targetType =  ObjectUtils.getTypeOf(field);

		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field, targetType);
		final Setter targetSetter = FieldSetter.from(field, targetType);

		return SourcePropertyBuilder.newInstance()
				.source(arraySource.get())
				.mode(AccessMode.READ_WRITE)
				.targetGetter(targetGetter)
				.targetSetter(targetSetter)
				.targetReference(PropertyMapper.isReference(targetSetter.getType()))
				.build(factory, typeAdapters);
	}
	
	Optional<SourceProperty> getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final Optional<SingleSource> source = 
					getSingleSource( 
						sourceAnnotation,
						field,
						defaultSourceClass
						); 
		
		if (source.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}
		
		ObjectType targetType =  ObjectUtils.getTypeOf(field);
		
		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field, targetType);
		final Setter targetSetter = FieldSetter.from(field, targetType);

		return SourcePropertyBuilder.newInstance()
					.source(source.get())
					.mode(sourceAnnotation.mode())
					.targetGetter(targetGetter)
					.targetSetter(targetSetter)
					.targetReference(PropertyMapper.isReference(targetSetter.getType()))					
					.build(factory, typeAdapters);
	}

	Optional<SingleSource> getSingleSource(Source sourceAnnotation, Field field, Class<?> defaultSourceClass) {
				
		Class<?> sourceObjectClass = defaultSourceClass;
		
		// override source property class
		if (!Class.class.equals(sourceAnnotation.type())) {
			sourceObjectClass = sourceAnnotation.type();
		}
		
		if (sourceObjectClass == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}

		// set default source object property name -> use the same name
		String sourceFieldName = field.getName();
				
		// override source property name
		if (StringUtils.isNotBlank(sourceAnnotation.value())) {
			sourceFieldName = sourceAnnotation.value();
		}
		
		SingleSourceBuilder sourceBuilder = SingleSourceBuilder.newInstance()
				.objectClass(sourceObjectClass)
				.optional(sourceAnnotation.optional())
				.qualifier(sourceAnnotation.name())
				.mode(sourceAnnotation.mode())
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

		return getSingleSource(
					sourceObjectClass, 
					sourceFieldName, 
					sourceBuilder
					);
	}
	
	Optional<SingleSource> getSingleSource(Class<?> sourceObjectClass, String sourceFieldName, SingleSourceBuilder sourceBuilder) {
		
		// extract setter/getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourceFieldName);
		final Setter sourceSetter = ObjectUtils.getSetter(sourceObjectClass, sourceFieldName);

		return sourceBuilder
				.getter(sourceGetter)
				.setter(sourceSetter)
				.build(typeAdapters);
	}

	Optional<ArraySource> getArraySource(final Sources sourcesAnnotation, final Field field, final Class<?> defaultSourceObjectClass) {
		
		SingleSource[] sources = Arrays.stream(sourcesAnnotation.value())
										.map(s -> getSingleSource(s, field, defaultSourceObjectClass))
										.flatMap(Optional::stream)
										.collect(Collectors.toList())
										.toArray(new SingleSource[0])
										;
		
		if (sources.length == 0) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return Optional.empty();
		}

		ArraySourceBuilder builder = 
				ArraySourceBuilder.newInstance()
					.optional(sourcesAnnotation.optional())
					.sources(sources)
					;

		try {
			return builder
						.converters(getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
						.build(typeAdapters);
			
		} catch (ConverterError | ProjectionError e) {
			logger.error("Property " + field.getName() + " is ignored.", e);
			return Optional.empty();
		}				
	}	
	
	Collection<ConverterMapping> getConverterMapping(Conversion[] conversions) throws ConverterError, ProjectionError {

		final List<ConverterMapping> converters = new ArrayList<>();
		
		if (conversions.length == 0) {
			return converters;
		}

		for (final Conversion conversion : conversions) {
			converters.add(
					ConversionBuilder
							.newInstance()
							.converter(conversion.type())
							.parameters(conversion.value())
							.build()
							);
		}

		return converters.isEmpty() ? null : converters;
	}	
}
