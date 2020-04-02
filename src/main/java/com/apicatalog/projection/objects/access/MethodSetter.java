package com.apicatalog.projection.objects.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import com.apicatalog.projection.ProjectionError;

public class MethodSetter implements Setter {

	final Method method;
	final String name;
	
	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	protected MethodSetter(Method method, String name) {
		this.method = method;
		this.name = name;
	}
	
	public static final MethodSetter from(Method method, String name) {
		
		final MethodSetter setter = new MethodSetter(method, name);
		
		setter.setValueClass(method.getReturnType());

		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			setter.setValueComponentClass((Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
		}
		
		return setter;
	}

	@Override
	public void set(Object object, Object value) throws ProjectionError {
		
		try {
			method.invoke(object, value);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			/* ignored */
		}
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
	public Object getName() {
		return name;
	}
	
}
