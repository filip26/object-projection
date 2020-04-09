package com.apicatalog.projection.adapter.type.legacy;

import com.apicatalog.projection.adapter.type.TypeAdapter;
import com.apicatalog.projection.adapter.type.TypeAdapterError;

public class Object2String implements TypeAdapter<Object> {

	@Override
	public Class<Object> consumes() {
		return Object.class;
	}

	@Override
	public Class<?>[] produces() {
		return new Class[] {String.class};
	}

	@Override
	public <T> T convert(Class<? extends T> targetClass, Object object) throws TypeAdapterError {
		if (object == null) {
			return (T)"null";
		}
		return (T)object.toString();
	}

	
	
}
