package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.InvertibleFunction;
import com.apicatalog.projection.mapping.ConversionMapping;

public class ConversionMappingImpl implements ConversionMapping {

	final Logger logger = LoggerFactory.getLogger(ConversionMappingImpl.class);

	//TODO !!! use ConverterFactory|Index!!!! a conversion utilizes a convertor
	
	final Class<? extends InvertibleFunction<?>> convertorClass;
	final String[] context;

	public ConversionMappingImpl(Class<? extends InvertibleFunction<?>> convertorClass, String[] context) {
		this.convertorClass = convertorClass;
		this.context = context;
	}
	
	@Override
	public Object forward(Object value) throws ConverterError, ProjectionError {
		
		logger.debug("{}.forward({}, {})", convertorClass.getSimpleName(), value, context);
		
		final InvertibleFunction<?> convertor = ObjectUtils.newInstance(convertorClass);	//TODO re-use preconstructed instances

		ContextValue ctx = new ContextValue();
		ctx.setValues(context);

		convertor.init(ctx);
			
		final Object result = convertor.compute(value);
		
		logger.trace("  result={}", result);
		
		return result;
	}

	@Override
	public Object backward(Object value) throws ConverterError, ProjectionError {

		logger.debug("{}.backward({}, {})", convertorClass.getSimpleName(), value, context);
		
		final InvertibleFunction<Object> convertor = (InvertibleFunction<Object>)ObjectUtils.newInstance(convertorClass);	//TODO re-use preconstructed instances

		ContextValue ctx = new ContextValue();
		ctx.setValues(context);

		convertor.init(ctx);
			
		final Object[] result = convertor.inverse(value);
		
		logger.trace("  result={}", result);
		
		return result;
	}	
}
