package com.apicatalog.projection.adapter.type.legacy;

import java.net.URI;

import com.apicatalog.projection.adapter.type.TypeAdapter;
import com.apicatalog.projection.adapter.type.TypeAdapterError;

public class UriAdapter implements TypeAdapter<URI> {

	@Override
	public Class<URI> consumes() {
		return URI.class;
	}

	@Override
	public Class<?>[] produces() {
		return new Class[] {String.class};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Class<? extends T> targetClass, URI object) throws TypeAdapterError {
		if (object == null) {
			return null;
		}
		
		if (targetClass.isAssignableFrom(String.class)) {
			return (T)object.toString();
		}

		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
	}
}
