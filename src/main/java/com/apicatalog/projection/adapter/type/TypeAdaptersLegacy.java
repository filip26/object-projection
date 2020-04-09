package com.apicatalog.projection.adapter.type;

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
import com.apicatalog.projection.adapter.type.legacy.BooleanAdapter;
import com.apicatalog.projection.adapter.type.legacy.DoubleAdapter;
import com.apicatalog.projection.adapter.type.legacy.FloatAdapter;
import com.apicatalog.projection.adapter.type.legacy.InstantAdapter;
import com.apicatalog.projection.adapter.type.legacy.IntegerAdapter;
import com.apicatalog.projection.adapter.type.legacy.LongAdapter;
import com.apicatalog.projection.adapter.type.legacy.StringAdapter;
import com.apicatalog.projection.adapter.type.legacy.UriAdapter;
import com.apicatalog.projection.objects.ObjectType;

@Deprecated
public class TypeAdaptersLegacy {

	final Logger logger = LoggerFactory.getLogger(TypeAdaptersLegacy.class);
	
	final Map<Class<?>, TypeAdapter<?>> adapters;
	
	public TypeAdaptersLegacy() {
		this.adapters = new LinkedHashMap<>();

		add(new StringAdapter())
			.add(new BooleanAdapter())
			.add(new LongAdapter())
			.add(new InstantAdapter())
			.add(new DoubleAdapter())
			.add(new IntegerAdapter())
			.add(new FloatAdapter())
			.add(new UriAdapter())
			;
	}

	@SuppressWarnings("unchecked")
	public <S> TypeAdapter<S> get(Class<? extends S> sourceClass) {
		return (TypeAdapter<S>) adapters.get(sourceClass);
	}

	public TypeAdaptersLegacy add(TypeAdapter<?> adapter) {
		adapters.put(adapter.consumes(), adapter);
		return this;
	}

	public Object convert(ObjectType objectType, Object object) throws ProjectionError {
		return convert(objectType.getType(),objectType.getComponentClass(), object);
	}
	
	public Object convert(Class<?> targetClass, Object object) throws ProjectionError {
		return convert(targetClass, null, object);
	}
	
	public Object convert(Class<?> targetClass, Class<?> componentClass, Object object) throws ProjectionError {
		
		if (object == null) {
			return null;
		}
		
		// no conversion needed
		if (targetClass.isInstance(object) && componentClass == null) {
			return object;
		}
		
		logger.debug("Convert {} : {} to {}", object, object.getClass().getSimpleName(), targetClass.getSimpleName());
		
		// collection to ?
		if (Collection.class.isInstance(object)) {
			
			// collection to collection
			if (componentClass != null) {				
				return collectionToCollection(targetClass, componentClass, (Collection<?>)object);
			}
			// collection to array
			if (targetClass.isArray()) {
				return collectiontoArray(targetClass.getComponentType(), (Collection<?>)object);
			}
			// one item collection?
			if (((Collection<?>)object).size() == 1) {
				object = ((Collection<?>)object).iterator().next();	// reduce to single object
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
			// one item array?
			if (((Object[])object).length== 1) {
				object = ((Object[])object)[0];	// reduce to single object
			}
		}

		final TypeAdapter<Object> adapter = get(object.getClass());
		
		if (adapter == null) {
			//TODO do implicit conversion to string via toString()
			throw new ProjectionError("Can not convert " + object.getClass() + " to " + targetClass + ".");
		}
		
		try {
			return adapter.convert(targetClass, object);
			
		} catch (TypeAdapterError e) {
			throw new ProjectionError(e);
		}
	}


	Collection<Object> collectionToCollection(Class<?> targetClass, Class<?> componentClass, Collection<?> objects) throws ProjectionError {
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

	Object[] collectiontoArray(Class<?> targetClass, Collection<?> objects) throws ProjectionError {
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
		
		// no conversion needed
		if (targetClass.isAssignableFrom(objects.getClass().getComponentType())) {
			return objects;
		}
		
		final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetClass, objects.length);
		
		for (int i=0; i < objects.length; i++) {
			converted[i] = convert(targetClass, objects[i]);
		}
		return converted;
	}
}
