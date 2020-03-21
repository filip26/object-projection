package com.apicatalog.projection.adapter;

import java.util.Objects;

public class TypeAdapterKey<T, S> {

	Class<? extends T> target;
	Class<? extends S> source;
	
	public static <T, S> Object of(Class<? extends T> targetClass, Class<? extends S> sourceClass) {
		TypeAdapterKey<T, S> key = new TypeAdapterKey<>();
		key.target = targetClass;
		key.source = sourceClass;
		return key;
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, target);
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
		TypeAdapterKey<?, ?> other = (TypeAdapterKey<?, ?>) obj;
		return Objects.equals(source, other.source) && Objects.equals(target, other.target);
	}
}
