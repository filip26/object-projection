package com.apicatalog.projection.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.mapper.mapping.ConversionMappingImpl;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.ConverterMapping;

public class ConversionMapper {

	final Logger logger = LoggerFactory.getLogger(ConversionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	public ConversionMapper(ProjectionFactory index, TypeAdapters typeAdapters) {
		this.factory = index;
		this.typeAdapters = typeAdapters;
	}
	
	ConversionMapping[] getConversionMapping(Conversion[] conversions) {

		if (conversions.length == 0) {
			return new ConversionMapping[0];
		}

		return Stream.of(conversions)
				.map(this::getConverterMapping)
				.collect(Collectors.toList())
				.toArray(new ConversionMapping[0])
				;		
	}
	
	ConversionMapping getConverterMapping(final Conversion conversion) {
		
		//FIXME use ConverterFactory
		ConverterMapping converter = new ConverterMapping();
		converter.setConverterClass(conversion.type());
		
		
		Type sourceType = ((ParameterizedType) conversion.type().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		
		Class<?> sourceClass = null;
		Class<?> sourceComponentClass = null;
		
		if (ParameterizedType.class.isInstance(sourceType)) {
			sourceClass = (Class<?>)((ParameterizedType)sourceType).getRawType();
			sourceComponentClass = (Class<?>)((ParameterizedType)sourceType).getActualTypeArguments()[0];
			
		} else {
			sourceClass = (Class<?>) sourceType;
		}
		

		converter.setSourceClass(sourceClass);
		converter.setSourceComponentClass(sourceComponentClass);

		Type targetType = ((ParameterizedType) conversion.type().getGenericInterfaces()[0]).getActualTypeArguments()[1];
		
		Class<?> targetClass = null;
		Class<?> targetComponentClass = null;
		
		if (ParameterizedType.class.isInstance(targetType)) {
			targetClass = (Class<?>)((ParameterizedType)targetType).getRawType();
			targetComponentClass = (Class<?>)((ParameterizedType)targetType).getActualTypeArguments()[0];
			
		} else {
			targetClass = (Class<?>) targetType;
		}

		converter.setTargetClass(targetClass);
		converter.setTargetComponentClass(targetComponentClass);
		
		return new ConversionMappingImpl(converter, typeAdapters, conversion.value());
	}
}
