package com.apicatalog.projection.converter;

//TODO use annotations
@Deprecated
public interface InvertibleFunction<T> {

	void init(ContextValue ctx) throws ConvertorError;
	
	T compute(Object...values) throws ConvertorError;
	
	Object[] inverse(T value) throws ConvertorError;
	
	boolean isReverseable();
	
}
