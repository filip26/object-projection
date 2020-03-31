package com.apicatalog.projection.objects;

import java.util.Objects;

import com.apicatalog.projection.NamedObject;

public class ObjectKey {

	final Class<?> clazz;
	final String qualifier;
	
	ObjectKey(Class<?> clazz, String qualifier) {
		this.clazz = clazz;
		this.qualifier = qualifier;
	}
	
	public static ObjectKey of(Class<?> clazz, String qualifier) {
		return new ObjectKey(clazz, qualifier == null ? "" : qualifier);
	}

	public static ObjectKey of(Object object) {
		if (object instanceof NamedObject) {
			final NamedObject<?> namedObject = (NamedObject<?>)object;
			
			return of(namedObject.getObject().getClass(), namedObject.getName());	
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
		ObjectKey other = (ObjectKey) obj;
		return Objects.equals(clazz, other.clazz) && Objects.equals(qualifier, other.qualifier);
	}	
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public String getQualifier() {
		return qualifier;
	}

	@Override
	public String toString() {
		return "ObjectKey [clazz=" + clazz + ", qualifier=" + qualifier + "]";
	}
}
