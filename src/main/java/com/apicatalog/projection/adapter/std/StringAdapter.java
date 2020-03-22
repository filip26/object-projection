package com.apicatalog.projection.adapter.std;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class StringAdapter implements TypeAdapter<String> {

	@Override
	public Class<String> source() {
		return String.class;
	}

	@Override
	public Class<?>[] targets() {
		return new Class[] {Long.class, Double.class, Boolean.class};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Class<? extends T> targetClass, String object) throws TypeAdapterError {
		
		if (object == null) {
			return null;
		}

		if (targetClass.isAssignableFrom(String.class)) {
			return (T)object;
		}
		if (targetClass.isAssignableFrom(Boolean.class)) {
			return (T)Boolean.valueOf(object);
		}
		if (targetClass.isAssignableFrom(Long.class)) {
			return (T)Long.valueOf(object);
		}		
		if (targetClass.isAssignableFrom(Double.class)) {
			return (T)Double.valueOf(object);
		}		
		
		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");

	}
}
