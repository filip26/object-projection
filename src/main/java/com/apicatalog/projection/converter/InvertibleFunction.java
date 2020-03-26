package com.apicatalog.projection.converter;

//TODO use annotations and Converter
@Deprecated
public interface InvertibleFunction<T> {

	void init(ContextValue ctx) throws ConverterError;
	
	T compute(Object...values) throws ConverterError;
	
	Object[] inverse(T value) throws ConverterError;
	
	boolean isReverseable();
	
}
