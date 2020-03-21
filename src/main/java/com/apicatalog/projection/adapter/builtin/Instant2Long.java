package com.apicatalog.projection.adapter.builtin;

import java.time.Instant;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class Instant2Long implements TypeAdapter<Long, Instant> {

	@Override
	public Class<Instant> source() {
		return Instant.class;
	}

	@Override
	public Class<Long> target() {
		return Long.class;
	}

	@Override
	public Long convert(Instant object) throws TypeAdapterError {
		if (object == null) {
			return null;
		}
		return object.toEpochMilli();
	}

}
