package com.apicatalog.projection.objects;

import java.util.Optional;

public final class ObjectType {

	final Class<?> objectClass;
	final Class<?> objectComponentClass;
	
	final String asString;
	
	final boolean reference;
	
	protected ObjectType(Class<?> objectClass, Class<?> objectComponentClass, boolean reference, String asString) { 
		this.objectClass = objectClass;
		this.objectComponentClass = objectComponentClass;
		this.reference = reference;
		this.asString = asString;
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
		
		StringBuilder builder = new StringBuilder();
		builder.append(ObjectType.class.getSimpleName());
		builder.append(" [");
		builder.append(objectClass.getSimpleName());
		Optional.ofNullable(objectComponentClass).ifPresent(c -> builder.append("<" + c.getSimpleName() + ">"));
		if (reference) { builder.append(", reference"); }
		builder.append(']');
		
		return new ObjectType(objectClass, objectComponentClass, reference, builder.toString());
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
	
	@Override
	public String toString() {
		return asString;
	}
}
