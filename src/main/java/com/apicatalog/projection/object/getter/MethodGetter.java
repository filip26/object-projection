package com.apicatalog.projection.object.getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.apicatalog.projection.object.ObjectType;

public final class MethodGetter implements Getter {

	final Method method;
	
	final String name;
	
	final ObjectType type;
	
	protected MethodGetter(final Method method, final String name, final ObjectType type) {
		this.method = method;
		this.name = name;
		this.type = type;
	}
	
	public static final MethodGetter from(final Method method, final String name, final ObjectType type) {
		return new MethodGetter(method, name, type);
	}

	@Override
	public Optional<Object> get(final Object object) {
		try {
			return Optional.ofNullable(method.invoke(object));
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { /* ignore */}
		
		return Optional.empty();
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
		return "MethodGetter [name=" + (method != null ? method.getName() : "n/a") + ", type=" + type + "]";
	}

}
