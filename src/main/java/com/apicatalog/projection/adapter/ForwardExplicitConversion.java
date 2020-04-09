package com.apicatalog.projection.adapter;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;

public class ForwardExplicitConversion implements Conversion<Object, Object> {

	final Converter<Object, Object> converter;
	
	protected ForwardExplicitConversion(Converter<Object, Object> converter) {
		this.converter = converter;
	}
	
	public static final ForwardExplicitConversion of(Converter<Object, Object> converter) {
		return new ForwardExplicitConversion(converter);
	}
	
	@Override
	public Object convert(Object object) throws ConverterError {
		return converter.forward(object);
	}

}
