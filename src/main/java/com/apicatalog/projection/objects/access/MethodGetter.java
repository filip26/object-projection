package com.apicatalog.projection.objects.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.apicatalog.projection.ProjectionError;

public class MethodGetter implements Getter {

	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	final Method method;
	final String name;
	
	public MethodGetter(Method method, String name) {
		this.method = method;
		this.name = name;
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
