package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.builder.ArraySourceBuilder;
import com.apicatalog.projection.builder.ConversionBuilder;
import com.apicatalog.projection.builder.ReductionBuilder;
import com.apicatalog.projection.builder.SingleSourceBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.getter.FieldGetter;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.FieldSetter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.source.ArraySource;
import com.apicatalog.projection.property.source.SingleSource;
import com.apicatalog.projection.reducer.ReducerError;
import com.apicatalog.projection.reducer.ReducerMapping;

class SourceMapper {

	final Logger logger = LoggerFactory.getLogger(SourceMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final TypeAdapters typeAdapters;
	final ProjectionRegistry factory;
	
	public SourceMapper(ProjectionRegistry index, TypeAdapters typeAdapters) {
		this.factory = index;
		this.typeAdapters = typeAdapters;
	}
		
	ProjectionProperty getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final ArraySource arraySource = getArraySource(sourcesAnnotation, field, defaultSourceClass);

		if (arraySource == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;				
		}
		
		ObjectType targetType = PropertyMapper.getTypeOf(field);

		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field, targetType);
		final Setter targetSetter = FieldSetter.from(field, targetType);

		return SourcePropertyBuilder.newInstance()
				.source(arraySource)
				.mode(AccessMode.READ_WRITE)
				.targetGetter(targetGetter)
				.targetSetter(targetSetter)
				.build(factory, typeAdapters);
	}
	
	ProjectionProperty getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final SingleSource source = 
					getSingleSource( 
						sourceAnnotation,
						field,
						defaultSourceClass
						); 
		
		if (source == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;
		}
		
		ObjectType targetType = PropertyMapper.getTypeOf(field);
		
		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field, targetType);
		final Setter targetSetter = FieldSetter.from(field, targetType);

		return SourcePropertyBuilder.newInstance()
					.source(source)
					.mode(sourceAnnotation.mode())
					.targetGetter(targetGetter)
					.targetSetter(targetSetter)
					.build(factory, typeAdapters);
	}

	SingleSource getSingleSource(Source sourceAnnotation, Field field, Class<?> defaultSourceClass) {
				
		Class<?> sourceObjectClass = defaultSourceClass;
		
		// override source property class
		if (!Class.class.equals(sourceAnnotation.type())) {
			sourceObjectClass = sourceAnnotation.type();
		}
		
		if (sourceObjectClass == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;
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
				.qualifier(sourceAnnotation.qualifier())
				.mode(sourceAnnotation.mode())
				;

		// set conversions to apply
		if (Optional.ofNullable(sourceAnnotation.map()).isPresent()) {
			try {
				sourceBuilder = sourceBuilder.converters(getConverterMapping(sourceAnnotation.map()));
				
			} catch (ConverterError | ProjectionError e) {
				logger.error("Property " + sourceFieldName + " is ignored.", e);
				return null;
			}
		}

		return getSingleSource(
					sourceObjectClass, 
					sourceFieldName, 
					sourceBuilder
					);
	}
	
	SingleSource getSingleSource(Class<?> sourceObjectClass, String sourceFieldName, SingleSourceBuilder sourceBuilder) {
		
		// extract setter/getter
		final Getter sourceGetter = PropertyMapper.getGetter(sourceObjectClass, sourceFieldName);
		final Setter sourceSetter = PropertyMapper.getSetter(sourceObjectClass, sourceFieldName);

		return sourceBuilder
				.getter(sourceGetter)
				.setter(sourceSetter)
				.build(typeAdapters);
	}

	ArraySource getArraySource(final Sources sourcesAnnotation, final Field field, final Class<?> defaultSourceObjectClass) {
		
		SingleSource[] sources = Arrays.stream(sourcesAnnotation.value())
										.map(s -> getSingleSource(s, field, defaultSourceObjectClass))
										.collect(Collectors.toList())
										.toArray(new SingleSource[0])
										;
		
		if (sources.length == 0) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;
		}

		ArraySourceBuilder builder = 
				ArraySourceBuilder.newInstance()
					.optional(sourcesAnnotation.optional())
					.sources(sources)
					;

		try {
			return builder
						.reducer(getReductionMapping(sourcesAnnotation.reduce()))	// set reduction
						.converters(getConverterMapping(sourcesAnnotation.map()))	// set conversions to apply
						.build(typeAdapters);
			
		} catch (ConverterError | ReducerError | ProjectionError e) {
			logger.error("Property " + field.getName() + " is ignored.", e);
			return null;
		}				
	}	
	
	ConverterMapping[] getConverterMapping(Conversion[] conversions) throws ConverterError, ProjectionError {

		if (conversions.length == 0) {
			return new ConverterMapping[0];
		}

		final List<ConverterMapping> converters = new ArrayList<>();
		
		for (final Conversion conversion : conversions) {
			converters.add(
					ConversionBuilder
							.newInstance()
							.converter(conversion.type())
							.parameters(conversion.value())
							.build()
							);
		}

		return converters.isEmpty() ? null : converters.toArray(new ConverterMapping[0]);
	}

	ReducerMapping getReductionMapping(Reduction reduction) throws ProjectionError, ReducerError {
		
		return ReductionBuilder
					.newInstance()
					.converter(reduction.type())
					.parameters(reduction.value())
					.build()
					;
	}	
}