package com.apicatalog.projection.adapter;

import java.util.LinkedHashMap;
import java.util.Map;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.std.BooleanAdapter;
import com.apicatalog.projection.adapter.std.DoubleAdapter;
import com.apicatalog.projection.adapter.std.FloatAdapter;
import com.apicatalog.projection.adapter.std.InstantAdapter;
import com.apicatalog.projection.adapter.std.IntegerAdapter;
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
			.add(new DoubleAdapter())
			.add(new IntegerAdapter())
			.add(new FloatAdapter())
			;
	}

	@SuppressWarnings("unchecked")
	public <S> TypeAdapter<S> get(Class<? extends S> sourceClass) {
		return (TypeAdapter<S>) adapters.get(sourceClass);
	}

	public TypeAdapters add(TypeAdapter<?> adapter) {
		adapters.put(adapter.consumes(), adapter);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T convert(Class<? extends T> targetClass, Object object) throws ProjectionError {
		
		// no conversion needed
		if (targetClass.isInstance(object)) {
			return (T)object;
		}

		final TypeAdapter<Object> adapter = get(object.getClass());
		
		if (adapter == null) {
			throw new ProjectionError("Can not convert " + object.getClass() + " to " + targetClass + ".");
		}
		
		try {
			return adapter.convert(targetClass, object);
			
		} catch (TypeAdapterError e) {
			throw new ProjectionError(e);
		}
	}
	
}
