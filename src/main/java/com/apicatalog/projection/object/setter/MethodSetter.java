package com.apicatalog.projection.object.setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.object.ObjectType;

public final class MethodSetter implements Setter {

	final Method method;
	
	final String name;
	
	final ObjectType type;
	
	protected MethodSetter(Method method, String name, ObjectType type) {
		this.method = method;
		this.name = name;
		this.type = type;
	}
	
	public static final MethodSetter from(Method method, String name, ObjectType type) {		
		return new MethodSetter(method, name, type);
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
	public String getName() {
		return name;
	}
	
	@Override
	public ObjectType getType() {
		return type;
	}
	
}
