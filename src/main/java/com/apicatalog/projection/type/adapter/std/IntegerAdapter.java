package com.apicatalog.projection.type.adapter.std;

import java.time.Instant;

import com.apicatalog.projection.type.adapter.TypeAdapter;
import com.apicatalog.projection.type.adapter.TypeAdapterError;

public class IntegerAdapter implements TypeAdapter<Integer> {

	@Override
	public Class<Integer> consumes() {
		return Integer.class;
	}

	@Override
	public Class<?>[] produces() {
		return new Class[] {String.class, Long.class, Short.class, Byte.class, Double.class, Float.class, Instant.class, Boolean.class};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Class<? extends T> targetClass, Integer object) throws TypeAdapterError {
		
		if (targetClass.isAssignableFrom(String.class)) {
			return (T)object.toString();
		}
		if (targetClass.isAssignableFrom(Long.class)) {
			return (T)Long.valueOf(object.longValue());
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
