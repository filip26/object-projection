package com.apicatalog.projection.adapter;

public interface TypeAdapter<T, S> {

	Class<S> source();
	Class<T> target();
	
	T convert(S object) throws TypeAdapterError;
	
}
