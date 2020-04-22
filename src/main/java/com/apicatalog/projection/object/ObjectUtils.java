package com.apicatalog.projection.object;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.getter.MethodGetter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.MethodSetter;
import com.apicatalog.projection.object.setter.Setter;

public final class ObjectUtils {

	ObjectUtils() {}
	
	public static final <T> T newInstance(final Class<? extends T> clazz) throws ObjectError {
		
		if (clazz == null) {
			throw new IllegalArgumentException();
		}
		
		try {
			return clazz.getDeclaredConstructor().newInstance();
					
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			throw new ObjectError("Can not instantiate class " + clazz.getCanonicalName(), e);
		}
	}
	
	static final Field getProperty(final Class<?> clazz, final String name) {

		if (clazz == null) {
			throw new IllegalArgumentException();
		}
		
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
		}

		try {
			Field field = clazz.getDeclaredField(name);

			if (!Modifier.isStatic(field.getModifiers())
					&& !Modifier.isTransient(field.getModifiers())) {
				return field;
			}

		} catch (NoSuchFieldException e) {/* ignore */}
		
		return null;
	}
	
	static final Method getMethod(final Class<?> clazz, final String name) {
				
		if (clazz == null) {
			throw new IllegalArgumentException();
		}
		
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
		}
		
		try {
			
			return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(name)).findFirst().orElse(null);
			
		} catch (SecurityException e) {/* ignore */}
		
		return null;
	}
	
	public static final Getter getGetter(final Class<?> clazz, final String name) throws ObjectError {

		if (clazz == null) {
			throw new IllegalArgumentException();
		}
		
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
		}
		
		final Field sourceField = ObjectUtils.getProperty(clazz, name);
		
		if (sourceField != null) {
			return FieldGetter.from(sourceField, getTypeOf(sourceField));
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(clazz, "get".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return MethodGetter.from(sourceGetter, name, getReturnTypeOf(sourceGetter));
		}

		throw new ObjectError("Can not get getter for " + name + ", class " + clazz.getCanonicalName());
	}
	
	public static final Setter getSetter(final Class<?> clazz, final String name) throws ObjectError {

		if (clazz == null) {
			throw new IllegalArgumentException();
		}
		
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
		}
		
		final Field sourceField = ObjectUtils.getProperty(clazz, name);
		
		if (sourceField != null) {
			return FieldSetter.from(sourceField, getTypeOf(sourceField));
		}

		// look for getter method
		final Method sourceSetter = ObjectUtils.getMethod(clazz, "set".concat(StringUtils.capitalize(name)));

		if (sourceSetter != null) {
			return MethodSetter.from(sourceSetter, name, getParameterTypeOf(sourceSetter));
		}

		throw new ObjectError("Can not get setter for " + name + ", class " + clazz.getCanonicalName());
	}

	public static final ObjectType getTypeOf(final Field field) {
		
		if (field == null) {
			throw new IllegalArgumentException();
		}
		
		Class<?> objectClass = field.getType();
		Class<?> componentClass = null;
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			componentClass = (Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass);
	}
	
	static final ObjectType getReturnTypeOf(final Method method) {
		
		if (method == null) {
			throw new IllegalArgumentException();
		}
		
		Class<?> objectClass = method.getReturnType();
		Class<?> componentClass = null;

		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			componentClass = (Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass);
	}
	
	static final ObjectType getParameterTypeOf(final Method method) {
		
		if (method == null) {
			throw new IllegalArgumentException();
		}
		
		Class<?> objectClass = method.getParameters()[0].getType();
		Class<?> componentClass = null;

		if (Collection.class.isAssignableFrom(method.getParameters()[0].getType())) {
			componentClass = (Class<?>)((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass);
	}
}
