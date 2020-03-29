package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.ConverterMapping;

public class ConversionMappingImpl implements ConversionMapping {

	final Logger logger = LoggerFactory.getLogger(ConversionMappingImpl.class);

	final ConverterMapping mapping;
	final TypeAdapters typeAdapters;
	final String[] config;

	public ConversionMappingImpl(ConverterMapping mapping, TypeAdapters typeAdapters, String[] config) {
		this.mapping = mapping;
		this.typeAdapters = typeAdapters;
		this.config = config;
	}
	
	@Override
	public Object forward(Object value) throws ProjectionError {
		
		logger.debug("{}.forward({}, {})", mapping.getConverterClass().getSimpleName(), value, config);
		
		final Converter<Object, Object> converter = (Converter<Object, Object>) ObjectUtils.newInstance(mapping.getConverterClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig();
		ctx.setValues(config);

		try {
			converter.initConverter(ctx);
				
			final Object result = converter.forward(typeAdapters.convert(mapping.getSourceClass(), value));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}

	@Override
	public Object backward(Object value) throws ProjectionError {

		logger.debug("{}.backward({}, {})", mapping.getConverterClass().getSimpleName(), value, config);
		
		final Converter<Object, Object> converter = (Converter<Object, Object>) ObjectUtils.newInstance(mapping.getConverterClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig();
		ctx.setValues(config);

		try {
			converter.initConverter(ctx);
				
			final Object result = converter.backward(typeAdapters.convert(mapping.getTargetClass(), value));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}

	@Override
	public Class<?> getSourceClass() {
		return mapping.getSourceClass();
	}

	@Override
	public Class<?> getTargetClass() {
		return mapping.getTargetClass();
	}	
}
