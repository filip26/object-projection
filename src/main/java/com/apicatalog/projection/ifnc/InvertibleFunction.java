package com.apicatalog.projection.ifnc;

//TODO use annotations
@Deprecated
public interface InvertibleFunction<T> {

	void init(ContextValue ctx);
	
	T compute(Object...values) throws InvertibleFunctionError;
	
	Object[] inverse(T value) throws InvertibleFunctionError;
	
	boolean isReverseable();
	
}
