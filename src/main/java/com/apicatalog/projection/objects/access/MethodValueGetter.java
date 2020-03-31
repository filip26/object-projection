package com.apicatalog.projection.objects.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.apicatalog.projection.ProjectionError;

public class MethodValueGetter implements ValueGetter {

	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	final Method method;
	
	public MethodValueGetter(Method method) {
		this.method = method;
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
}
