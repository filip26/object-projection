package com.apicatalog.projection.adapter;

public interface TypeAdapter<S> {

	Class<S> source();
	
	Class<?>[] targets();
	
	<T> T convert(Class<? extends T> targetClass, S object) throws TypeAdapterError;
	
}
