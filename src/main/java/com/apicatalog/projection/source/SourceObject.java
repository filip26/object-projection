package com.apicatalog.projection.source;

import java.util.Optional;

public final class SourceObject {

	final Object object;
	final String name;
	
	SourceObject(String qualifier, Object object) {
		this.object = object;
		this.name = qualifier;
	}
	
	public static SourceObject of(String qualifier, Object object) {
		return new SourceObject(qualifier, object);
	}
	
	public Object getObject() {
		return object;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SourceObject [object=" + Optional.ofNullable(object).map(Object::getClass).map(Class::getSimpleName).orElse("n/a") + ", name=" + name + "]";
	}
}
