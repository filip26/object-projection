package com.apicatalog.projection.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;

public class ConversionMappingBuilder {

	final Logger logger = LoggerFactory.getLogger(ConversionMappingBuilder.class);
	
	Class<?> converterClass;
	String[] parameters;
	
	protected ConversionMappingBuilder() {
	}
	
	public static final ConversionMappingBuilder newInstance() {
		return new ConversionMappingBuilder();
	}
		
	public ConverterMapping build() throws ConverterError, ProjectionError {
		
		ConverterMapping converter = new ConverterMapping();
		
		@SuppressWarnings("unchecked")
		Converter<Object, Object> instance = (Converter<Object, Object>) ObjectUtils.newInstance(converterClass);
		
		instance.initConverter(new ConverterConfig(parameters));
		
		converter.setConverter(instance);

		Type sourceType = ((ParameterizedType) converterClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
		
		Class<?> sourceClass = null;
		Class<?> sourceComponentClass = null;
		
		if (ParameterizedType.class.isInstance(sourceType)) {
			sourceClass = (Class<?>)((ParameterizedType)sourceType).getRawType();
			sourceComponentClass = (Class<?>)((ParameterizedType)sourceType).getActualTypeArguments()[0];
			
		} else {
			sourceClass = (Class<?>) sourceType;
		}
		
		converter.setSourceType(ObjectType.of(sourceClass, sourceComponentClass));

		Type targetType = ((ParameterizedType) converterClass.getGenericInterfaces()[0]).getActualTypeArguments()[1];
		
		Class<?> targetClass = null;
		Class<?> targetComponentClass = null;
		
		if (ParameterizedType.class.isInstance(targetType)) {
			targetClass = (Class<?>)((ParameterizedType)targetType).getRawType();
			targetComponentClass = (Class<?>)((ParameterizedType)targetType).getActualTypeArguments()[0];
			
		} else {
			targetClass = (Class<?>) targetType;
		}

		converter.setTargetType(ObjectType.of(targetClass, targetComponentClass));

		return converter;		
	}
	
	public ConversionMappingBuilder converter(Class<?> converterClass) {
		this.converterClass = converterClass;
		return this;
	}
	
	public ConversionMappingBuilder parameters(String[] parameters) {
		this.parameters = parameters;
		return this;
	}
}
