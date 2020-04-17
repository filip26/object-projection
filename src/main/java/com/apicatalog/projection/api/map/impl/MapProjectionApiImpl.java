package com.apicatalog.projection.api.map.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.impl.MapProjection;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjectionApiImpl implements MapProjectionApi {
	
	final List<MapEntryApiImpl> entries;
	
	protected MapProjectionApiImpl() {
		this.entries = new ArrayList<>();
	}
	
	public static final MapProjectionApi hashMap() {
		return new MapProjectionApiImpl();
	}

	@Override
	public MapEntryApi map(final String name, final Class<?> type) {
		return map(name, type, null);
	}

	@Override
	public MapEntryApi map(final String name, final Class<?> type, Class<?> componentType) {
		
		final MapEntryApiImpl propertyBuilder = new MapEntryApiImpl(this, name, type, componentType);
		entries.add(propertyBuilder);
		
		return propertyBuilder;
	}
	
	@Override
	public Projection<Map<String, Object>> build(final ProjectionRegistry factory) throws ProjectionBuilderError {

		final List<PropertyReader> readers = new ArrayList<>(); 
		final List<PropertyWriter> writers = new ArrayList<>();
		
		for (final MapEntryApiImpl entry : entries) {
			entry
					.buildReader(factory)
					.ifPresent(readers::add);
			
			entry
					.buildWriter(factory)
					.ifPresent(writers::add);
		}
		
		if (readers.isEmpty() && writers.isEmpty()) {
			return null;
		}
		
		final Projection<Map<String, Object>> projection = 
					MapProjection.newInstance(
									readers.toArray(new PropertyReader[0]), 
									writers.toArray(new PropertyWriter[0])
									);

		factory.register(projection);
		
		return projection;
	}

	protected Class<?> projectionClass() {
		return Map.class;
	}
}