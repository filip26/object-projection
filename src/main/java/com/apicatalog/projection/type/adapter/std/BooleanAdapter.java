package com.apicatalog.projection.type.adapter.std;

import com.apicatalog.projection.type.adapter.TypeAdapter;
import com.apicatalog.projection.type.adapter.TypeAdapterError;

public class BooleanAdapter implements TypeAdapter<Boolean> {

	@Override
	public Class<Boolean> consumes() {
		return Boolean.class;
	}

	@Override
	public Class<?>[] produces() {
		return new Class[] {String.class, Byte.class, Long.class, Double.class, Character.class, Integer.class, Float.class};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Class<? extends T> targetClass, Boolean object) throws TypeAdapterError {
		if (object == null) {
			return null;
		}
		
		if (targetClass.isAssignableFrom(String.class)) {
			return (T)object.toString();
		}
		if (targetClass.isAssignableFrom(Long.class)) {
			return (T)(Boolean.TRUE.equals(object) ? Long.valueOf(1l) : Long.valueOf(0l));
		}
		if (targetClass.isAssignableFrom(Byte.class)) {
			return (T)(Boolean.TRUE.equals(object) ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0));
		}
		if (targetClass.isAssignableFrom(Double.class)) {
			return (T)(Boolean.TRUE.equals(object) ? Double.valueOf(1.0d) : Double.valueOf(0.0d));
		}
		if (targetClass.isAssignableFrom(Character.class)) {
			return (T)(Boolean.TRUE.equals(object) ? Character.valueOf('T') : Character.valueOf('F'));
		}
		if (targetClass.isAssignableFrom(Integer.class)) {
			return (T)(Boolean.TRUE.equals(object) ? Integer.valueOf(1) : Integer.valueOf(0));
		}
		if (targetClass.isAssignableFrom(Float.class)) {
			return (T)(Boolean.TRUE.equals(object) ? Float.valueOf(1.0f) : Float.valueOf(0.0f));
		}

		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
	}
}
