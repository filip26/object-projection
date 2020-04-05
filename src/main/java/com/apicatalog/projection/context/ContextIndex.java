package com.apicatalog.projection.context;

import java.util.Objects;
import java.util.Optional;

import com.apicatalog.projection.objects.NamedObject;

final class ContextIndex {

	final Class<?> clazz;
	final String qualifier;
	
	ContextIndex(Class<?> clazz, String qualifier) {
		this.clazz = clazz;
		this.qualifier = qualifier;
	}
	
	public static ContextIndex of(Class<?> clazz, String qualifier) {
		return new ContextIndex(clazz, qualifier == null ? "" : qualifier);		
	}
	
	public static ContextIndex of(Object object) {
		if (NamedObject.class.isInstance(object)) {
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
		ContextIndex other = (ContextIndex) obj;
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
		return "ContextIndex [clazz=" + Optional.ofNullable(clazz).map(Class::getSimpleName).orElse("n/a") + ", qualifier=" +Optional.ofNullable(qualifier).orElse("n/a") + "]";
	}
}
