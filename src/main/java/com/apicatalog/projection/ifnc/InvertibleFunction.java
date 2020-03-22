package com.apicatalog.projection.ifnc;

public interface InvertibleFunction<T> {

	void init(ContextValue ctx);
	
	T compute(Object...values) throws InvertibleFunctionError;
	
	Object[] inverse(T value) throws InvertibleFunctionError;
	
}
