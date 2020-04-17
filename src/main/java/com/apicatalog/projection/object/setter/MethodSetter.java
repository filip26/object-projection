package com.apicatalog.projection.object.setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.object.ObjectType;

public final class MethodSetter implements Setter {

	final Method method;
	
	final String name;
	
	final ObjectType type;
	
	protected MethodSetter(final Method method, final String name, final ObjectType type) {
		this.method = method;
		this.name = name;
		this.type = type;
	}
	
	public static final MethodSetter from(final Method method, final String name, final ObjectType type) {		
		return new MethodSetter(method, name, type);
	}

	@Override
	public void set(final Object object, final Object value) throws ProjectionError {
		
		try {
			method.invoke(object, value);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			/* ignored */
		}
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public ObjectType getType() {
		return type;
	}	
	
	@Override
	public String toString() {
		return "MethodSetter [name=" + (method != null ? method.getName() : "n/a") + ", type=" + type + "]";
	}
}