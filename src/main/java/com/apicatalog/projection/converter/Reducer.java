package com.apicatalog.projection.converter;

public interface Reducer {

	void init(ContextValue ctx) throws ReducerError;
	
	Object reduce(Object...objects) throws ReducerError;
	
	Object[] expand(Object object) throws ReducerError;
	
}
