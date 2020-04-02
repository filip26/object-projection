package com.apicatalog.projection.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import com.apicatalog.projection.ProjectionError;

public class MethodGetter implements Getter {

	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	final Method method;
	final String name;
	
	protected MethodGetter(Method method, String name) {
		this.method = method;
		this.name = name;
	}
	
	public static final MethodGetter from(Method method, String name) {
		
		final MethodGetter setter = new MethodGetter(method, name);
		
		setter.setValueClass(method.getReturnType());

		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			setter.setValueComponentClass((Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
		}
		
		return setter;
	}

	@Override
	public Object get(final Object object) throws ProjectionError {
		try {
			return method.invoke(object);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { /* ignore */}
		
		return null;
	}

	@Override
	public Class<?> getValueClass() {
		return valueClass;
	}
	
	@Override
	public Class<?> getValueComponentClass() {
		return valueComponentClass;
	}
	
	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}
	
	public void setValueComponentClass(Class<?> valueComponentClass) {
		this.valueComponentClass = valueComponentClass;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
