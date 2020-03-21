package com.apicatalog.projection.adapter.builtin;

import com.apicatalog.projection.adapter.TypeAdapter;

public class Long2String implements TypeAdapter<String, Long> {

	@Override
	public Class<Long> source() {
		return Long.class;
	}

	@Override
	public Class<String> target() {
		return String.class;
	}

	@Override
	public String convert(Long object) {
		if (object == null) {
			return null;
		}
		return object.toString();
	}

}
