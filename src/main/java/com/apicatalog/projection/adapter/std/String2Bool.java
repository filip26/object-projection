package com.apicatalog.projection.adapter.std;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class String2Bool implements TypeAdapter<Boolean, String> {

	@Override
	public Class<String> source() {
		return String.class;
	}

	@Override
	public Class<Boolean> target() {
		return Boolean.class;
	}

	@Override
	public Boolean convert(String object) throws TypeAdapterError {
		
		return Boolean.valueOf(object);
		
	}

}
