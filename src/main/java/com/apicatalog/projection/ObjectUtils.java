package com.apicatalog.projection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;

public class ObjectUtils {

	ObjectUtils() {}
	
	public static Object getPropertyValue(Object object, String property) throws ProjectionError {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException();
		}

		try {
			
			final Field field = object.getClass().getDeclaredField(property);
			field.setAccessible(true);
			return field.get(object);

		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			throw new ProjectionError("Can not get property=" + property + " value of " + object.getClass() + ".", e);
		}
	}

	public static void setPropertyValue(Object object, String property, Object value) throws ProjectionError {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException();
		}
	
		try {
			final Field field = object.getClass().getDeclaredField(property);
			field.setAccessible(true);			
			field.set(object, value);
			
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new ProjectionError("Can not set property=" + property + " value of " + object.getClass().getCanonicalName() + " to " + value + ".", e);
		}
	}
	
	public static <T> T newInstance(Class<? extends T> clazz) throws ProjectionError {
		try {
			
			return clazz.getDeclaredConstructor().newInstance();
					
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			throw new ProjectionError("Can not instantiate " + clazz + ".", e);
		}
	}
}
