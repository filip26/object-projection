package com.apicatalog.projection.object;

import java.io.Serializable;
import java.util.Optional;

public final class ObjectType implements Serializable {

	private static final long serialVersionUID = -4456253218795281471L;

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
	
	public boolean isAssignableFrom(final ObjectType object) {
		return Object.class == type || (type.isAssignableFrom(object.type) 
				&& ((componentType == null)
					? (object.componentType == null)
					: (object.componentType != null) && componentType.isAssignableFrom(object.componentType)))
					;
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
