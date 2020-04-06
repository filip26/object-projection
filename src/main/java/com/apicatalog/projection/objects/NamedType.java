package com.apicatalog.projection.objects;

import java.util.Objects;
import java.util.Optional;

public final class NamedType<T> {

	final Class<T> type;
	final String name;
	
	NamedType(String name, Class<T> type) {
		this.type = type;
		this.name = name;
	}
	
	public static <T> NamedType<T> of(String name, Class<T> type) {
		return new NamedType<>(name, type);
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "NamedType [type=" + Optional.ofNullable(type).map(Class::getSimpleName).orElse("n/a") + ", name=" + Optional.ofNullable(name).orElse("n/a") + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
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
		NamedType<?> other = (NamedType<?>) obj;
		return Objects.equals(name, other.name) && Objects.equals(type, other.type);
	}
}
