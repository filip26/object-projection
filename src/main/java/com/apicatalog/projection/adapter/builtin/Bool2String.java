package com.apicatalog.projection.adapter.builtin;

import com.apicatalog.projection.adapter.TypeAdapter;

public class Bool2String implements TypeAdapter<String, Boolean> {

	@Override
	public Class<Boolean> source() {
		return Boolean.class;
	}

	@Override
	public Class<String> target() {
		return String.class;
	}

	@Override
	public String convert(Boolean object) {
		if (object == null) {
			return null;
		}
		return object.toString();
	}

}
