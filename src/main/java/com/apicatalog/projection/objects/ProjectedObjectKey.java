package com.apicatalog.projection.objects;

import java.util.Objects;

public class ProjectedObjectKey {

	final Class<?> clazz;
	final String qualifier;
	
	ProjectedObjectKey(Class<?> clazz, String qualifier) {
		this.clazz = clazz;
		this.qualifier = qualifier;
	}
	
	public static ProjectedObjectKey of(Class<?> clazz, String qualifier) {
		return new ProjectedObjectKey(clazz, qualifier == null ? "" : qualifier);
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
		ProjectedObjectKey other = (ProjectedObjectKey) obj;
		return Objects.equals(clazz, other.clazz) && Objects.equals(qualifier, other.qualifier);
	}	
}
