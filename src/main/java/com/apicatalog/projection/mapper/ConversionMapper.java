package com.apicatalog.projection.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.ConverterMapping;
import com.apicatalog.projection.objects.ObjectUtils;

public class ConversionMapper {

	final Logger logger = LoggerFactory.getLogger(ConversionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	public ConversionMapper(ProjectionFactory index, TypeAdapters typeAdapters) {
		this.factory = index;
		this.typeAdapters = typeAdapters;
	}
	
	ConverterMapping[] getConverterMapping(Conversion[] conversions) throws ConverterError, ProjectionError {

		if (conversions.length == 0) {
			return new ConverterMapping[0];
		}

		List<ConverterMapping> converters = new ArrayList<>();
		
		for (Conversion conversion : conversions) {
			converters.add(getConverterMapping(conversion));
		}
		
		return converters.toArray(new ConverterMapping[0]);
	}
	
	ConverterMapping getConverterMapping(final Conversion conversionAnnotation) throws ConverterError, ProjectionError {
		
		ConverterMapping converter = new ConverterMapping();
		
		Converter<Object, Object> instance = (Converter<Object, Object>) ObjectUtils.newInstance(conversionAnnotation.type());
		
		instance.initConverter(new ConverterConfig(conversionAnnotation.value()));
		
		converter.setConverter(instance);

		Type sourceType = ((ParameterizedType) conversionAnnotation.type().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		
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

		Type targetType = ((ParameterizedType) conversionAnnotation.type().getGenericInterfaces()[0]).getActualTypeArguments()[1];
		
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
		
		return converter;
			
	}
}
