package com.apicatalog.projection.objects.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;

public class MethodSetter implements Setter {

	TypeAdapters typeAdapters;

	final Method method;
	final String name;
	
	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	public MethodSetter(Method method, String name) {
		this.method = method;
		this.name = name;
	}

	@Override
	public void set(Object object, Object value) throws ProjectionError {
		
		try {
			method.invoke(object, typeAdapters.convert(valueClass, valueComponentClass, value));
			
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
