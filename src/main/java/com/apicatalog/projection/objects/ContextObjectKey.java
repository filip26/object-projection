package com.apicatalog.projection.objects;

import java.util.Objects;

import com.apicatalog.projection.NamedObject;

public class ContextObjectKey {

	final Class<?> clazz;
	final String qualifier;
	
	ContextObjectKey(Class<?> clazz, String qualifier) {
		this.clazz = clazz;
		this.qualifier = qualifier;
	}
	
	public static ContextObjectKey of(Class<?> clazz, String qualifier) {
		return new ContextObjectKey(clazz, qualifier == null ? "" : qualifier);
	}

	public static ContextObjectKey of(Object object) {
		if (object instanceof NamedObject) {
			NamedObject<?> namedObject = (NamedObject<?>)object;
			
			return new ContextObjectKey(namedObject.getObject().getClass(), namedObject.getName()) ;	
		}
		return of(object.getClass(), null);
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(clazz, qualifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ContextObjectKey other = (ContextObjectKey) obj;
		return Objects.equals(clazz, other.clazz) && Objects.equals(qualifier, other.qualifier);
	}	
}
