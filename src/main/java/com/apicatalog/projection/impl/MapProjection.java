package com.apicatalog.projection.impl;

import java.util.Map;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjection extends AbstractProjection<Map<String, Object>> {
	
	final String name;
	
	protected MapProjection(final String name, final MapProjectionComposer composer, final MapProjectionExtractor extractor) {
		super(composer, extractor);
		this.name = name;
	}
	
	public static final Projection<Map<String, Object>> newInstance(final String name, final PropertyReader[] readers, final PropertyWriter[] writers) {
		return new MapProjection(
						name, 
						MapProjectionComposer.newInstance(writers),
						MapProjectionExtractor.newInstance(readers)
						);
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final Class<?> getType() {
		return Map.class;
	}
}