package com.apicatalog.projection.adapter.std;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class String2Long implements TypeAdapter<Long, String> {

	@Override
	public Class<String> source() {
		return String.class;
	}

	@Override
	public Class<Long> target() {
		return Long.class;
	}

	@Override
	public Long convert(String object) throws TypeAdapterError {
		if (object == null) {
			return null;
		}
		
		try {
			return Long.valueOf(object.trim());
			
		} catch (NumberFormatException e) {
			throw new TypeAdapterError(e);
		}
	}

}
