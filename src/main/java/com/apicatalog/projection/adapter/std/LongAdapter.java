package com.apicatalog.projection.adapter.std;

import java.time.Instant;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class LongAdapter implements TypeAdapter<Long> {

	@Override
	public Class<Long> consumes() {
		return Long.class;
	}

	@Override
	public Class<?>[] produces() {
		return new Class[] {String.class, Integer.class, Short.class, Byte.class, Double.class, Float.class, Instant.class, Boolean.class};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Class<? extends T> targetClass, Long object) throws TypeAdapterError {
		
		if (targetClass.isAssignableFrom(String.class)) {
			return (T)object.toString();
		}
		if (targetClass.isAssignableFrom(Integer.class)) {
			return (T)Integer.valueOf(object.intValue());
		}		
		if (targetClass.isAssignableFrom(Short.class)) {
			return (T)Short.valueOf(object.shortValue());
		}		
		if (targetClass.isAssignableFrom(Byte.class)) {
			return (T)Byte.valueOf(object.byteValue());
		}		
		if (targetClass.isAssignableFrom(Double.class)) {
			return (T)Double.valueOf(object.doubleValue());
		}		
		if (targetClass.isAssignableFrom(Float.class)) {
			return (T)Float.valueOf(object.floatValue());
		}
		if (targetClass.isAssignableFrom(Instant.class)) {
			return (T)Instant.ofEpochMilli(object);
		}
		if (targetClass.isAssignableFrom(Boolean.class)) {
			return (T)(object == 1 ? Boolean.TRUE : Boolean.FALSE);
		}		
		
		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
	}

}
