package com.apicatalog.projection.converter;

public interface Converter {

	void init(ContextValue ctx) throws ConverterError;
	
	Object forward(Object object) throws ConverterError;
	
	Object backward(Object object) throws ConverterError;
}
