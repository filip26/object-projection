package com.apicatalog.projection;

import java.util.Objects;

public class SourceKey {

	final Class<?> clazz;
	final String qualifier;
	
	SourceKey(Class<?> clazz, String qualifier) {
		this.clazz = clazz;
		this.qualifier = qualifier;
	}
	
	public static SourceKey of(Class<?> clazz, String qualifier) {
		return new SourceKey(clazz, qualifier == null ? "" : qualifier);
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
		SourceKey other = (SourceKey) obj;
		return Objects.equals(clazz, other.clazz) && Objects.equals(qualifier, other.qualifier);
	}	
}
