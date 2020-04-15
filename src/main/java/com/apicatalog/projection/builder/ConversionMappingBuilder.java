package com.apicatalog.projection.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.converter.LambdaConverter;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;

public final class ConversionMappingBuilder {

	final Logger logger = LoggerFactory.getLogger(ConversionMappingBuilder.class);
	
	Class<? extends Converter<?, ?>> converterClass;
	String[] parameters;
	
	Class<?> sourceType;
	Class<?> targetType;
	
	Conversion<Object, Object> forward;
	Conversion<Object, Object> backward;
	
	protected ConversionMappingBuilder() {
	}
	
	public static final ConversionMappingBuilder newInstance() {
		return new ConversionMappingBuilder();
	}
		
	public ConverterMapping build() throws ConverterError, ProjectionError {
		
		final ConverterMapping converter = new ConverterMapping();
		
		if (forward != null || backward != null) {
			converter.setSourceType(ObjectType.of(sourceType));
			converter.setTargetType(ObjectType.of(targetType));
			converter.setConverter(new LambdaConverter<>(forward, backward));
			
			return converter;
		}
		
		@SuppressWarnings("unchecked")
		Converter<Object, Object> instance = (Converter<Object, Object>) ObjectUtils.newInstance(converterClass);
		
		instance.initConverter(new ConverterConfig(parameters));
		
		converter.setConverter(instance);

		Type sourceNativeType = ((ParameterizedType) converterClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
		
		Class<?> sourceClass = null;
		Class<?> sourceComponentClass = null;
		
		if (ParameterizedType.class.isInstance(sourceNativeType)) {
			sourceClass = (Class<?>)((ParameterizedType)sourceNativeType).getRawType();
			sourceComponentClass = (Class<?>)((ParameterizedType)sourceNativeType).getActualTypeArguments()[0];
			
		} else {
			sourceClass = (Class<?>) sourceNativeType;
		}
		
		converter.setSourceType(ObjectType.of(sourceClass, sourceComponentClass));

		Type targetNativeType = ((ParameterizedType) converterClass.getGenericInterfaces()[0]).getActualTypeArguments()[1];
		
		Class<?> targetClass = null;
		Class<?> targetComponentClass = null;
		
		if (ParameterizedType.class.isInstance(targetNativeType)) {
			targetClass = (Class<?>)((ParameterizedType)targetNativeType).getRawType();
			targetComponentClass = (Class<?>)((ParameterizedType)targetNativeType).getActualTypeArguments()[0];
			
		} else {
			targetClass = (Class<?>) targetNativeType;
		}

		converter.setTargetType(ObjectType.of(targetClass, targetComponentClass));

		return converter;		
	}
	
	public ConversionMappingBuilder converter(Class<? extends Converter<?, ?>> converterClass) {
		this.converterClass = converterClass;
		return this;
	}
	
	public ConversionMappingBuilder parameters(String[] parameters) {
		this.parameters = parameters;
		return this;
	}
	
	public ConversionMappingBuilder types(Class<?> source, Class<?> target) {
		this.sourceType = source;
		this.targetType = target;
		return this;
	}

	public ConversionMappingBuilder forward(Conversion<Object, Object> conversion) {
		this.forward = conversion;
		return this;
	}

	public ConversionMappingBuilder backward(Conversion<Object, Object> conversion) {
		this.backward = conversion;
		return this;
	}

}