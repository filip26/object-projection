package com.apicatalog.projection.adapter;

import java.util.LinkedHashMap;
import java.util.Map;

import com.apicatalog.projection.adapter.std.BooleanAdapter;
import com.apicatalog.projection.adapter.std.InstantAdapter;
import com.apicatalog.projection.adapter.std.LongAdapter;
import com.apicatalog.projection.adapter.std.StringAdapter;

public class TypeAdapters {

	final Map<Class<?>, TypeAdapter<?>> adapters;
	
	public TypeAdapters() {
		this.adapters = new LinkedHashMap<>();

		add(new StringAdapter())
		.add(new BooleanAdapter())
		.add(new LongAdapter())
		.add(new InstantAdapter())
		;
	}
	
	@SuppressWarnings("unchecked")
	public <S> TypeAdapter<S> get(Class<? extends S> sourceClass) {
		return (TypeAdapter<S>) adapters.get(sourceClass);
	}

	public TypeAdapters add(TypeAdapter<?> adapter) {
		adapters.put(adapter.source(), adapter);
		return this;
	}
	
}
