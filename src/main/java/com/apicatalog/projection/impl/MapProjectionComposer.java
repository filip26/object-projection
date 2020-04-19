package com.apicatalog.projection.impl;

import java.util.HashMap;
import java.util.Map;

import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjectionComposer extends  AbstractProjectionComposer<Map<String, Object>> {
	
	protected MapProjectionComposer(final String projectionName, final PropertyWriter[] writers) {
		super(projectionName, writers);
	}
	
	public static final MapProjectionComposer newInstance(final String projectionName, final PropertyWriter[] writers) {
		return new MapProjectionComposer(projectionName, writers);
	}

	@Override
	protected final Map<String, Object> newInstance() {
		return new HashMap<>();
	}
}
