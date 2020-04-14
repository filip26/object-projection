package com.apicatalog.projection.object;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.getter.MethodGetter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.MethodSetter;
import com.apicatalog.projection.object.setter.Setter;

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
	
	public static final Getter getGetter(Class<?> objectClass, final String name) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldGetter.from(sourceField, getTypeOf(sourceField));
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(objectClass, "get".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return MethodGetter.from(sourceGetter, name, getReturnTypeOf(sourceGetter));
		}

		return null;
	}
	
	public static final Setter getSetter(Class<?> objectClass, final String name) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldSetter.from(sourceField, getTypeOf(sourceField));
		}

		// look for getter method
		final Method sourceSetter = ObjectUtils.getMethod(objectClass, "set".concat(StringUtils.capitalize(name)));

		if (sourceSetter != null) {
			return MethodSetter.from(sourceSetter, name, getParameterTypeOf(sourceSetter));
		}

		return null;
	}

	public static final ObjectType getTypeOf(Field field) {
		
		Class<?> objectClass = field.getType();
		Class<?> componentClass = null;
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			componentClass = (Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass);
	}
	
	protected static final ObjectType getReturnTypeOf(Method method) {
		
		Class<?> objectClass = method.getReturnType();
		Class<?> componentClass = null;

		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			componentClass = (Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass);
	}
	
	protected static final ObjectType getParameterTypeOf(Method method) {
		
		Class<?> objectClass = method.getParameters()[0].getType();
		Class<?> componentClass = null;

		if (Collection.class.isAssignableFrom(method.getParameters()[0].getType())) {
			componentClass = (Class<?>)((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass);
	}
}
