package com.apicatalog.projection.converter;

public interface Reducer<S, T> {

	void initReducer(ConverterConfig ctx) throws ReducerError;
	
	T reduce(S[] objects) throws ReducerError;
	
	S[] expand(T object) throws ReducerError;
	
}
