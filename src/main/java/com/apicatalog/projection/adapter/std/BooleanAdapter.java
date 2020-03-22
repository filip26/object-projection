package com.apicatalog.projection.adapter.std;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class BooleanAdapter implements TypeAdapter<Boolean> {

	@Override
	public Class<Boolean> source() {
		return Boolean.class;
	}

	@Override
	public Class<?>[] targets() {
		return new Class[] {String.class, Byte.class, Long.class, Double.class, Character.class};
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

		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
	}
}