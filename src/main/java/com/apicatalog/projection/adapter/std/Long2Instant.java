package com.apicatalog.projection.adapter.std;

import java.time.Instant;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;

public class Long2Instant implements TypeAdapter<Instant, Long> {

	@Override
	public Class<Long> source() {
		return Long.class;
	}

	@Override
	public Class<Instant> target() {
		return Instant.class;
	}

	@Override
	public Instant convert(Long object) throws TypeAdapterError {
		if (object == null) {
			return null;
		}
		return Instant.ofEpochMilli(object);
	}

}
