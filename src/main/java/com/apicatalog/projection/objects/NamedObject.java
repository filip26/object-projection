package com.apicatalog.projection.objects;

public final class NamedObject<T> {

	final T object;
	final String name;
	
	NamedObject(String name, T object) {
		this.object = object;
		this.name = name;
	}
	
	public static <T> NamedObject<T> of(String name, T object) {
		return new NamedObject<>(name, object);
	}
	
	public T getObject() {
		return object;
	}
	
	public String getName() {
		return name;
	}
	
}
