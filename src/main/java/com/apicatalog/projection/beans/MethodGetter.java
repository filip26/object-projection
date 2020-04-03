package com.apicatalog.projection.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ObjectType;

public class MethodGetter implements Getter {

	final Method method;
	
	final String name;
	
	final ObjectType type;
	
	protected MethodGetter(Method method, String name, ObjectType type) {
		this.method = method;
		this.name = name;
		this.type = type;
	}
	
	public static final MethodGetter from(Method method, String name, ObjectType type) {
		return new MethodGetter(method, name, type);
	}

	@Override
	public Object get(final Object object) throws ProjectionError {
		try {
			return method.invoke(object);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { /* ignore */}
		
		return null;
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
