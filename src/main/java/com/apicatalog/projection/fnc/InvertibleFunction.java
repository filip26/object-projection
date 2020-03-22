package com.apicatalog.projection.fnc;

public interface InvertibleFunction<T> {

	void init(ContextValue ctx);
	
	T compute(Object...values) throws InvertibleFunctionError;
	
	Object[] inverse(T value) throws InvertibleFunctionError;
	
}
