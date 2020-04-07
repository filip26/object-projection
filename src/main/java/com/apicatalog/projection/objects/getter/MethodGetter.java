package com.apicatalog.projection.objects.getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.apicatalog.projection.objects.ObjectType;

public final class MethodGetter implements Getter {

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
}
