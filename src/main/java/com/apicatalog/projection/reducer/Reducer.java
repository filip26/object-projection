package com.apicatalog.projection.reducer;

public interface Reducer<S, T> {

	void initReducer(ReducerConfig ctx) throws ReducerError;
	
	T reduce(S[] objects) throws ReducerError;
	
	S[] expand(T object) throws ReducerError;	
}
