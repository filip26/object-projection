package com.apicatalog.projection.objects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.apicatalog.projection.ProjectionError;

public final class ObjectUtils {

	ObjectUtils() {}
	
	public static <T> T newInstance(Class<? extends T> clazz) throws ProjectionError {
		try {
			return clazz.getDeclaredConstructor().newInstance();
					
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			throw new ProjectionError("Can not instantiate " + clazz + ".", e);
		}
	}
	
	public static Field getProperty(Class<?> clazz, String property) {
		
		try {
			Field field = clazz.getDeclaredField(property);

			if (!Modifier.isStatic(field.getModifiers())
					&& !Modifier.isTransient(field.getModifiers())) {
				return field;
			}

		} catch (NoSuchFieldException e) {/* ignore */}
		
		return null;
	}
	
	public static Method getMethod(Class<?> clazz, String name) {
				
		try {
			
			return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(name)).findFirst().orElse(null);
			
		} catch (SecurityException e) {/* ignore */}
		
		return null;
	}
}
