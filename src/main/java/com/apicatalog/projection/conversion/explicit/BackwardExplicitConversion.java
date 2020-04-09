package com.apicatalog.projection.conversion.explicit;

import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;

public class BackwardExplicitConversion implements Conversion<Object, Object> {

	final Converter<Object, Object> converter;
	
	protected BackwardExplicitConversion(Converter<Object, Object> converter) {
		this.converter = converter;
	}
	
	public static final BackwardExplicitConversion of(Converter<Object, Object> converter) {
		return new BackwardExplicitConversion(converter);
	}
	
	@Override
	public Object convert(Object object) throws ConverterError {
		return converter.backward(object);
	}

}
