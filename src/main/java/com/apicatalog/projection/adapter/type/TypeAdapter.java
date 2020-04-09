package com.apicatalog.projection.adapter.type;

public interface TypeAdapter<S> {

	Class<S> consumes();
	
	Class<?>[] produces();
	
	<T> T convert(Class<? extends T> targetClass, S object) throws TypeAdapterError;
	
}
