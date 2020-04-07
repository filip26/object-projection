package com.apicatalog.projection.objects;

import java.util.Optional;

public final class ObjectType {

	final Class<?> objectClass;
	final Class<?> objectComponentClass;
	
	final boolean reference;
	
	protected ObjectType(Class<?> objectClass, Class<?> objectComponentClass, boolean reference) { 
		this.objectClass = objectClass;
		this.objectComponentClass = objectComponentClass;
		this.reference = reference;
	}
	
	public static final ObjectType of(Class<?> objectClass) {
		return of(objectClass, null, false);
	}

	public static final ObjectType of(Class<?> objectClass, boolean reference) {
		return of(objectClass, null, reference);
	}
	
	public static final ObjectType of(Class<?> objectClass, Class<?> objectComponentClass) {
		return of(objectClass, objectComponentClass, false);
	}

	public static final ObjectType of(Class<?> objectClass, Class<?> objectComponentClass, boolean reference) {
		return new ObjectType(objectClass, objectComponentClass, reference);
	}

	public boolean isReference() {
		return reference;
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

	public boolean isInstance(Object object) {
		return (((objectComponentClass != null) && objectComponentClass.isInstance(object))
				|| ((objectClass != null) && objectClass.isInstance(object))
				);
	}
	
	public boolean isAssignableFrom(ObjectType type) {
		return (((objectComponentClass != null) && objectComponentClass.isAssignableFrom(type.getObjectComponentClass()))
				|| ((objectClass != null) && objectClass.isAssignableFrom(type.getObjectClass()))
				);		
	}

	public boolean isAssignableFrom(Class<?> type) {
		return (((objectComponentClass != null) && objectComponentClass.isAssignableFrom(type))
				|| ((objectClass != null) && objectClass.isAssignableFrom(type))
				);		
	}

	@Override
	public String toString() {
		return "ObjectType [" 
					+ Optional.ofNullable(objectClass).map(Class::getSimpleName).orElse("") 
					+ "<" 
					+ Optional.ofNullable(objectComponentClass).map(Class::getSimpleName).orElse("") 
					+ ">, reference=" 
					+ reference 
					+ "]"
					;
	}
}
