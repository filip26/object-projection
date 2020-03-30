package com.apicatalog.projection.objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;

public class MethodValueSetter implements ValueSetter {

	final TypeAdapters typeAdapters;

	final Method method;

	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	public MethodValueSetter(TypeAdapters typeAdapters, Method method) {
		this.typeAdapters = typeAdapters;
		this.method = method;
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
}
