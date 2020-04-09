package com.apicatalog.projection.object;

import java.util.Optional;

public final class ObjectType {

	final Class<?> type;
	final Class<?> componentType;
	
	protected ObjectType(Class<?> type, Class<?> componentType) { 
		this.type = type;
		this.componentType = componentType;
	}
	
	public static final ObjectType of(Class<?> type) {
		return of(type, null);
	}

	public static final ObjectType of(Class<?> type, Class<?> componentTYpe) {
		return new ObjectType(type, componentTYpe);
	}

	public boolean isCollection() {
		return componentType != null;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Class<?> getComponentClass() {
		return componentType;
	}

	@Override
	public String toString() {
		return  Optional.ofNullable(type).map(Class::getSimpleName).orElse("") 
					+ Optional.ofNullable(componentType).map(c -> "<" + c.getSimpleName() + ">").orElse("") 
					;
	}

	public boolean isArray() {
		return type.isArray();
	}
}
