package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.ConversionMapping;

public class ConversionMappingImpl implements ConversionMapping {

	final Logger logger = LoggerFactory.getLogger(ConversionMappingImpl.class);

	//TODO !!! use ConverterFactory|Index!!!! a conversion utilizes a convertor
	
	final Class<? extends Converter> converterClass;
	final String[] context;

	public ConversionMappingImpl(Class<? extends Converter> converterClass, String[] context) {
		this.converterClass = converterClass;
		this.context = context;
	}
	
	@Override
	public Object forward(Object value) throws ProjectionError {
		
		logger.debug("{}.forward({}, {})", converterClass.getSimpleName(), value, context);
		
		final Converter converter = ObjectUtils.newInstance(converterClass);	//TODO re-use preconstructed instances

		ContextValue ctx = new ContextValue();
		ctx.setValues(context);

		try {
			converter.init(ctx);
				
			final Object result = converter.forward(value);
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}

	@Override
	public Object backward(Object value) throws ProjectionError {

		logger.debug("{}.backward({}, {})", converterClass.getSimpleName(), value, context);
		
		final Converter converter = ObjectUtils.newInstance(converterClass);	//TODO re-use preconstructed instances

		ContextValue ctx = new ContextValue();
		ctx.setValues(context);

		try {
			converter.init(ctx);
				
			final Object result = converter.backward(value);
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}	
}
