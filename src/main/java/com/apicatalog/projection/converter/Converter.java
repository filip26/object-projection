package com.apicatalog.projection.converter;

public interface Converter<S, T> {

	void initConverter(ConverterConfig ctx) throws ConverterError;
	
	T forward(S object) throws ConverterError;
	
	S backward(T object) throws ConverterError;
	
}
