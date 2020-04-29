package com.apicatalog.projection.api.map.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.impl.MapProjection;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjectionApiImpl implements MapProjectionBuilderApi {
	
	final List<MapEntryBuildApi> entries;
	
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
		return map(name, String.class);
	}

	@Override
	public MapEntryApi mapInteger(String name) {
		return map(name, Integer.class);
	}

	@Override
	public MapEntryApi mapLong(String name) {
		return map(name, Long.class);
	}

	@Override
	public MapEntryApi mapFloat(String name) {
		return map(name, Float.class);
	}

	@Override
	public MapEntryApi mapDouble(String name) {
		return map(name, Double.class);
	}

	@Override
	public MapEntryApi mapBoolean(String name) {
		return map(name, Boolean.class);
	}

	@Override
	public MapEntryApi mapDate(String propertyName) {
		return map(propertyName, Date.class);
	}

	@Override
	public MapEntryApi mapInstant(String propertyName) {
		return map(propertyName, Instant.class);
	}
	
	@Override
	public MapEntryApi map(String name, Class<?> objectType) {
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
	public MapEntryApi ref(String name, Class<?> projectionType) {
		return ref(name, projectionType.getCanonicalName());
	}

	@Override
	public MapEntryApi ref(String name, String projectionName) {
		final MapRefEntryApiImpl propertyBuilder = new MapRefEntryApiImpl(this, name, projectionName, false);
		entries.add(propertyBuilder);
		return propertyBuilder;		
	}

	@Override
	public MapEntryApi refArray(String name, Class<?> projectionType) {
		return refArray(name, projectionType.getCanonicalName());
	}

	@Override
	public MapEntryApi refArray(String name, String projectionName) {
		final MapRefEntryApiImpl propertyBuilder = new MapRefEntryApiImpl(this, name, projectionName, true);
		entries.add(propertyBuilder);
		return propertyBuilder;		
	}

	@Override
	public MapEntryApi refCollection(String name, Class<?> collectionType, Class<?> projectionType) {
		return refCollection(name, collectionType, projectionType.getCanonicalName());
	}

	@Override
	public MapEntryApi refCollection(String name, Class<?> collectionType, String projectionName) {
		final MapRefEntryApiImpl propertyBuilder = new MapRefEntryApiImpl(this, name, collectionType, projectionName);
		entries.add(propertyBuilder);
		return propertyBuilder;		
	}
	
	@Override
	public Projection<Map<String, Object>> build(final Registry factory) throws ProjectionError {

		final List<PropertyReader> readers = new ArrayList<>(); 
		final List<PropertyWriter> writers = new ArrayList<>();
		
		for (final MapEntryBuildApi entry : entries) {
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