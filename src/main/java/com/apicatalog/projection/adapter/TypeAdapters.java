package com.apicatalog.projection.adapter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
		
		logger.debug("Convert {} : {} to {}", object, object.getClass().getSimpleName(), targetClass.getSimpleName());
		
		// collection to ?
		if (Collection.class.isInstance(object)) {
			
			// collection to collection
			if (componentClass != null) {				
				return collectionToCollection(targetClass, componentClass, (Collection<Object>)object);
			}
			// collection to array
			if (targetClass.isArray()) {
				return collectiontoArray(targetClass.getComponentType(), (Collection<Object>)object);
			}
		}
		
		// array to ?
		if (object.getClass().isArray()) {
			// array to collection
			if (componentClass != null) {				
				return arrayToCollection(targetClass, componentClass, (Object[])object);
			}
			// array to array
			if (targetClass.isArray()) {
				return arrayToArray(targetClass.getComponentType(), (Object[])object);
			}
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

	Collection<Object> collectionToCollection(Class<?> targetClass, Class<?> componentClass, Collection<Object> objects) throws ProjectionError {
		logger.debug("Convert {} to {}<{}>", objects, targetClass.getSimpleName(), componentClass.getSimpleName());
		
		if (objects == null || objects.isEmpty()) {
			return Collections.emptyList();
		}
		
		Collection<Object> converted = null;
		
		if (Set.class.isAssignableFrom(targetClass)) {
			converted = new LinkedHashSet<>(objects.size());
		
		} else {
			converted = new ArrayList<>(objects.size());
		}
		
		for (Object object : objects) {
			converted.add(convert(componentClass, object));
		}

		return converted;
	}

	Collection<Object> arrayToCollection(Class<?> targetClass, Class<?> componentClass, Object[] objects) throws ProjectionError {
		logger.debug("Convert {} to {}<{}>", objects, targetClass.getSimpleName(), componentClass.getSimpleName());

		if (objects == null || objects.length == 0) {
			return Collections.emptyList();
		}
		
		Collection<Object> converted = null;
		
		if (Set.class.isAssignableFrom(targetClass)) {
			converted = new LinkedHashSet<>(objects.length);
		
		} else {
			converted = new ArrayList<>(objects.length);
		}
		
		for (Object object : objects) {
			converted.add(convert(componentClass, object));
		}

		return converted;
	}

	Object[] collectiontoArray(Class<?> targetClass, Collection<Object> objects) throws ProjectionError {
		logger.debug("Convert {} to {}[]", objects, targetClass.getSimpleName());

		if (objects == null || objects.isEmpty()) {
			return new Object[0];
		}
		
		final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetClass, objects.size());

		int index = 0;
		for (Object object : objects) {
			converted[index++] = convert(targetClass, object);
		}
		
		return converted;
	}
	
	Object[] arrayToArray(Class<?> targetClass, Object[] objects) throws ProjectionError {

		logger.debug("Convert {} to {}[]", objects, targetClass.getSimpleName());

		if (objects == null || objects.length == 0) {
			return new Object[0];
		}
		
		final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetClass, objects.length);
		
		for (int i=0; i < objects.length; i++) {
			converted[i] = convert(targetClass, objects[i]);
		}
		return converted;
	}
}
