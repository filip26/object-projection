package com.apicatalog.projection.adapter.type.legacy;

import com.apicatalog.projection.adapter.type.TypeAdapter;
import com.apicatalog.projection.adapter.type.TypeAdapterError;

public class DoubleAdapter implements TypeAdapter<Float> {

	@Override
	public Class<Float> consumes() {
		return Float.class;
	}

	@Override
	public Class<?>[] produces() {
		return new Class[] {String.class, Integer.class, Short.class, Byte.class, Long.class, Double.class, Boolean.class};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Class<? extends T> targetClass, Float object) throws TypeAdapterError {
		
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
		if (targetClass.isAssignableFrom(Long.class)) {
			return (T)Long.valueOf(object.longValue());
		}		
		if (targetClass.isAssignableFrom(Double.class)) {
			return (T)Double.valueOf(object.doubleValue());
		}
		if (targetClass.isAssignableFrom(Boolean.class)) {
			return (T)(object == 1 ? Boolean.TRUE : Boolean.FALSE);
		}				
		
		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
	}

}
