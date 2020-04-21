package com.apicatalog.projection.api.map.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.impl.MapProjection;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjectionApiImpl implements MapProjectionBuilderApi {
	
	final List<MapEntryApiImpl> entries;
	
	final String projectionName;
	
	protected MapProjectionApiImpl(final String projectionName) {
		this.projectionName = projectionName;
		this.entries = new ArrayList<>();
	}
	
	public static final MapProjectionApi hashMap(final String name) {
		return new MapProjectionApiImpl(name);
	}

	@Override
	public MapEntryApi mapString(String name) {
		return mapObject(name, String.class);
	}

	@Override
	public MapEntryApi mapInteger(String name) {
		return mapObject(name, Integer.class);
	}

	@Override
	public MapEntryApi mapLong(String name) {
		return mapObject(name, Long.class);
	}

	@Override
	public MapEntryApi mapFloat(String name) {
		return mapObject(name, Float.class);
	}

	@Override
	public MapEntryApi mapDouble(String name) {
		return mapObject(name, Double.class);
	}

	@Override
	public MapEntryApi mapBoolean(String name) {
		return mapObject(name, Boolean.class);
	}

	@Override
	public MapEntryApi mapObject(String name, Class<?> objectType) {
		final MapEntryApiImpl propertyBuilder = new MapEntryApiImpl(this, name, objectType);
		entries.add(propertyBuilder);
		return propertyBuilder;		
	}

	@Override
	public MapEntryApi mapCollection(String name, Class<?> collectionType, Class<?> componentType) {
		final MapEntryApiImpl propertyBuilder = new MapEntryApiImpl(this, name, collectionType, componentType);
		entries.add(propertyBuilder);
		return propertyBuilder;
	}

	@Override
	public MapEntryApi mapReference(String name, Class<?> projectionType) {
		return mapReference(name, projectionType.getCanonicalName());
	}

	@Override
	public MapEntryApi mapReference(String name, String projectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapEntryApi mapReference(String name, Class<?> collectionType, Class<?> projectionType) {
		return mapReference(name, collectionType, projectionType.getCanonicalName());
	}

	@Override
	public MapEntryApi mapReference(String name, Class<?> collectionType, String projectionName) {
		// TODO Auto-generated method stub
		return null;
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
									projectionName,
									readers.toArray(new PropertyReader[0]), 
									writers.toArray(new PropertyWriter[0])
									);

		if (StringUtils.isNotBlank(projectionName)) {
			factory.register(projection);
		}
		
		return projection;
	}

	protected Class<?> projectionClass() {
		return Map.class;
	}
}