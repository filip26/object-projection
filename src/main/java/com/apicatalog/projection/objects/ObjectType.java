package com.apicatalog.projection.objects;

import java.util.Optional;

public final class ObjectType {

	final Class<?> objectClass;
	final Class<?> objectComponentClass;
	
	protected ObjectType(Class<?> objectClass, Class<?> objectComponentClass) { 
		this.objectClass = objectClass;
		this.objectComponentClass = objectComponentClass;
	}
	
	public static final ObjectType of(Class<?> objectClass) {
		return of(objectClass, null);
	}

	public static final ObjectType of(Class<?> objectClass, Class<?> objectComponentClass) {
		return new ObjectType(objectClass, objectComponentClass);
	}

	public boolean isCollection() {
		return objectComponentClass != null;
	}
	
	public Class<?> getObjectClass() {
		return objectClass;
	}
	
	public Class<?> getObjectComponentClass() {
		return objectComponentClass;
	}

	@Override
	public String toString() {
		return "ObjectType [" 
					+ Optional.ofNullable(objectClass).map(Class::getSimpleName).orElse("") 
					+ "<" 
					+ Optional.ofNullable(objectComponentClass).map(Class::getSimpleName).orElse("") 
					+ ">]" 
					;
	}
}
