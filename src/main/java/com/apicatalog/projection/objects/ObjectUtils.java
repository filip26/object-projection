package com.apicatalog.projection.objects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.ProjectionError;

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
	
	public static Class<?> getPropertyType(Class<?> clazz, String property)  {
		
		try {
			return clazz.getDeclaredField(property).getType();
			
		} catch (NoSuchFieldException e) {/* ignore */}
		
		return null;

	}
	
	public static boolean hasPropery(final Class<?> clazz, final String property) {
		try {
			Field field = clazz.getDeclaredField(property);

			return  !Modifier.isStatic(field.getModifiers())
					&& !Modifier.isTransient(field.getModifiers())
					;
			
		} catch (NoSuchFieldException | SecurityException e) {/* ignore */}
		
		return false;
	}

	public static Field getProperty(Class<?> clazz, String property) {
		
		try {
			return clazz.getDeclaredField(property);
			
		} catch (NoSuchFieldException e) {/* ignore */}
		
		return null;
	}
	
	public static Method getMethod(Class<?> clazz, String name) {
				
		try {
			return clazz.getDeclaredMethod(name);
			
		} catch (NoSuchMethodException | SecurityException e) {/* ignore */}
		
		return null;
	}
	
}
