package com.apicatalog.projection.reducer;

import com.apicatalog.projection.converter.ConverterConfig;

public interface Reducer<S, T> {

	void initReducer(ConverterConfig ctx) throws ReducerError;
	
	T reduce(S[] objects) throws ReducerError;
	
	S[] expand(T object) throws ReducerError;
	
}
