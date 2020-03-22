package com.apicatalog.projection.adapter;

import java.util.LinkedHashMap;
import java.util.Map;

import com.apicatalog.projection.adapter.std.Bool2String;
import com.apicatalog.projection.adapter.std.Instant2Long;
import com.apicatalog.projection.adapter.std.Long2Instant;
import com.apicatalog.projection.adapter.std.Long2String;
import com.apicatalog.projection.adapter.std.String2Bool;
import com.apicatalog.projection.adapter.std.String2Long;

public class TypeAdapters {

	final Map<TypeAdapterKey<?, ?>, TypeAdapter<?, ?>> adapters;
	
	public TypeAdapters() {
		this.adapters = new LinkedHashMap<>();
		
		add(new Long2String())
		.add(new String2Long())
		.add(new Instant2Long())
		.add(new Long2Instant())
		.add(new Bool2String())
		.add(new String2Bool())
		;

	}
	
	@SuppressWarnings("unchecked")
	public <T, S> TypeAdapter<T, S> get(Class<? extends T> targetClass, Class<? extends S> sourceClass) {
		return (TypeAdapter<T, S>) adapters.get(TypeAdapterKey.of(targetClass, sourceClass));
	}

	public TypeAdapters add(TypeAdapter<?, ?> adapter) {
		adapters.put((TypeAdapterKey<?, ?>) TypeAdapterKey.of(adapter.target(), adapter.source()), adapter);
		
		return this;
	}
	
}
