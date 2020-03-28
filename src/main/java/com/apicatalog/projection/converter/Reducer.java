package com.apicatalog.projection.converter;

public interface Reducer {

	void initReducer(ConverterConfig ctx) throws ReducerError;
	
	Object reduce(Object...objects) throws ReducerError;
	
	Object[] expand(Object object) throws ReducerError;
	
}
