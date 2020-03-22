package com.apicatalog.projection.adapter.std;

import java.time.Instant;
import java.util.Date;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class InstantAdapter implements TypeAdapter<Instant> {

	@Override
	public Class<Instant> source() {
		return Instant.class;
	}

	@Override
	public Class<?>[] targets() {
		return new Class[] {String.class, Long.class, Date.class};
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Class<? extends T> targetClass, Instant object) throws TypeAdapterError {
		if (object == null) {
			return null;
		}

		if (targetClass.isAssignableFrom(String.class)) {
			return (T)object.toString();
		}
		if (targetClass.isAssignableFrom(Long.class)) {
			return (T)Long.valueOf(object.toEpochMilli());
		}
		if (targetClass.isAssignableFrom(Date.class)) {
			return (T)Date.from(object);
		}
		
		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
	}
}
