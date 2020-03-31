package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.ConverterMapping;
import com.apicatalog.projection.objects.ObjectUtils;

public class ConversionMappingImpl implements ConversionMapping {

	final Logger logger = LoggerFactory.getLogger(ConversionMappingImpl.class);

	final ConverterMapping converterMapping;
	final TypeAdapters typeAdapters;
	final String[] config;

	public ConversionMappingImpl(ConverterMapping mapping, TypeAdapters typeAdapters, String[] config) {
		this.converterMapping = mapping;
		this.typeAdapters = typeAdapters;
		this.config = config;
	}
	
	@Override
	public Object forward(Object value) throws ProjectionError {
		
		logger.debug("{}.forward({}, {})", converterMapping.getConverterClass().getSimpleName(), value, config);
		
		final Converter<Object, Object> converter = (Converter<Object, Object>) ObjectUtils.newInstance(converterMapping.getConverterClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig();
		ctx.setValues(config);

		try {
			converter.initConverter(ctx);
				
			final Object result = converter.forward(typeAdapters.convert(converterMapping.getSourceClass(), converterMapping.getSourceComponentClass(), value));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}

	@Override
	public Object backward(Object value) throws ProjectionError {

		logger.debug("{}.backward({}, {})", converterMapping.getConverterClass().getSimpleName(), value, config);
		
		final Converter<Object, Object> converter = (Converter<Object, Object>) ObjectUtils.newInstance(converterMapping.getConverterClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig();
		ctx.setValues(config);

		try {
			converter.initConverter(ctx);
				
			final Object result = converter.backward(typeAdapters.convert(converterMapping.getTargetClass(), converterMapping.getTargetComponentClass(), value));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}
	
	@Override
	public ConverterMapping getConverterMapping() {
		return converterMapping;
	}
	
}
