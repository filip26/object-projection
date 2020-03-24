package com.apicatalog.projection.converter;

//TODO use annotations
@Deprecated
public interface InvertibleFunction<T> {

	void init(ContextValue ctx) throws InvertibleFunctionError;
	
	T compute(Object...values) throws InvertibleFunctionError;
	
	Object[] inverse(T value) throws InvertibleFunctionError;
	
	boolean isReverseable();
	
}
