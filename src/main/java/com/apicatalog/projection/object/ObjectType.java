package com.apicatalog.projection.object;

import java.util.Optional;

public final class ObjectType {

	final Class<?> type;
	final Class<?> componentType;
	
	protected ObjectType(final Class<?> type, final Class<?> componentType) { 
		this.type = type;
		this.componentType = componentType;
	}
	
	public static final ObjectType of(final Class<?> type) {
		return of(type, null);
	}

	public static final ObjectType of(final Class<?> type, final Class<?> componentTYpe) {
		return new ObjectType(type, componentTYpe);
	}

	public boolean isCollection() {
		return componentType != null;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Class<?> getComponentType() {
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
