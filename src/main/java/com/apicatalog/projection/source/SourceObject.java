package com.apicatalog.projection.source;

import java.util.Optional;

public final class SourceObject {

	final String name;
	
	final Object object;
	
	protected SourceObject(final String name, final Object object) {
		this.name = name;
		this.object = object;
	}
	
	public static SourceObject of(final String name, final Object object) {
		return new SourceObject(name, object);
	}
	
	public Object getObject() {
		return object;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {	
		return "SourceObject [object=" 
					+ Optional.ofNullable(object).map(Object::getClass).map(Class::getSimpleName).orElse("n/a") 
					+ ", name=" 
					+ Optional.ofNullable(name).orElse("n/a") 
					+ "]"
					;
	}
}
