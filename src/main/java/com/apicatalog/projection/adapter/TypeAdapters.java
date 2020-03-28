package com.apicatalog.projection.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.std.BooleanAdapter;
import com.apicatalog.projection.adapter.std.DoubleAdapter;
import com.apicatalog.projection.adapter.std.FloatAdapter;
import com.apicatalog.projection.adapter.std.InstantAdapter;
import com.apicatalog.projection.adapter.std.IntegerAdapter;
import com.apicatalog.projection.adapter.std.LongAdapter;
import com.apicatalog.projection.adapter.std.StringAdapter;

public class TypeAdapters {

	final Logger logger = LoggerFactory.getLogger(TypeAdapters.class);
	
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
	public Object convert(Class<?> targetClass, Object object) throws ProjectionError {
		return convert(targetClass, null, object);
	}
	
	public Object convert(Class<?> targetClass, Class<?> componentClass, Object object) throws ProjectionError {
		
		if (object == null) {
			return null;
		}
		
		// no conversion needed
		if (targetClass.isInstance(object)) {
			return object;
		}
		
		logger.debug("Convert {} to {}", object, targetClass.getSimpleName());
		
		if (object.getClass().isArray()) {
			if (componentClass != null) {
				return convertArrayToCollection(targetClass, componentClass, (Object[])object);
			}
			return convertArray(targetClass, (Object[])object);
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

	Collection<Object> convertArrayToCollection(Class<?> targetClass, Class<?> componentClass, Object[] objects) throws ProjectionError {
		logger.debug("Convert {} to {}<{}>", objects, targetClass.getSimpleName(), componentClass.getSimpleName());
		
		//TODO check target collection and choose
		
		final Collection<Object> converted = new ArrayList<>();
		
		for (Object object : objects) {
			converted.add(convert(componentClass, object));
		}

		return converted;
	}
	
	Object convertArray(Class<?> targetClass, Object[] objects) throws ProjectionError {

		logger.debug("Convert {} to {}[]", objects, targetClass.getSimpleName());

		final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetClass, objects.length);
		
		for (int i=0; i < objects.length; i++) {
			converted[i] = convert(targetClass, objects[i]);
		}
		return converted;
	}
}
