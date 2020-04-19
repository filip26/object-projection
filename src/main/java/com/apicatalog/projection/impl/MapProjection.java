package com.apicatalog.projection.impl;

import java.util.Map;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjection extends AbstractProjection<Map<String, Object>> {
	
	final String name;
	
	protected MapProjection(final String name, final ProjectionComposer<Map<String, Object>> composer, final ProjectionExtractor<Map<String, Object>> extractor) {
		super(composer, extractor);
		this.name = name;
	}
	
	public static final Projection<Map<String, Object>> newInstance(final String name, final PropertyReader[] readers, final PropertyWriter[] writers) {
		return new MapProjection(
						name, 
						MapProjectionComposer.newInstance(name, writers),
						ProjectionExtractorImpl.newInstance(name, readers)
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