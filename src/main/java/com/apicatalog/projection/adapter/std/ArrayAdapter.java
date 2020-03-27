package com.apicatalog.projection.adapter.std;

import java.util.Arrays;
import java.util.Collection;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class ArrayAdapter implements TypeAdapter<Object[]> {

	@Override
	public Class<Object[]> consumes() {
		return Object[].class;
	}

	@Override
	public Class<?>[] produces() {
		return new Class<?>[] { Collection.class };
	}

	@Override
	public <T> T convert(Class<? extends T> targetClass, Object[] object) throws TypeAdapterError {

		if (object.getClass().isArray()) {
			return (T)Arrays.asList(object);
		}

		throw new TypeAdapterError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
	}
	

}
