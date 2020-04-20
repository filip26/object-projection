package com.apicatalog.projection.api.map.impl;

import java.util.Map;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionApi;

public class MapProjectionApiWrapper implements MapProjectionApi {
	
	final MapProjectionApi projectionBuilder;
	
	protected MapProjectionApiWrapper(final MapProjectionApi projectionBuilder) {
		this.projectionBuilder = projectionBuilder;
	}
	
	@Override
	public MapEntryApi mapString(String name) {
		return projectionBuilder.mapString(name);
	}
	
	@Override
	public MapEntryApi mapInteger(String name) {
		return projectionBuilder.mapInteger(name);
	}
	
	@Override
	public MapEntryApi mapLong(String name) {
		return projectionBuilder.mapLong(name);
	}

	@Override
	public MapEntryApi mapFloat(String name) {
		return projectionBuilder.mapFloat(name);
	}
	
	@Override
	public MapEntryApi mapDouble(String name) {
		return projectionBuilder.mapDouble(name);
	}
	
	@Override
	public MapEntryApi mapBoolean(String name) {
		return projectionBuilder.mapBoolean(name);
	}
	
	@Override
	public MapEntryApi mapObject(String name, Class<?> objectType) {
		return projectionBuilder.mapObject(name, objectType);
	}
	
	@Override
	public MapEntryApi mapCollection(String name, Class<?> collectionType, Class<?> componentType) {
		return projectionBuilder.mapCollection(name, collectionType, componentType);
	}
	
	@Override
	public MapEntryApi mapReference(String name, Class<?> projectionType) {
		return projectionBuilder.mapReference(name, projectionType);
	}
	
	@Override
	public MapEntryApi mapReference(String name, String projectionName) {
		return projectionBuilder.mapReference(name, projectionName);
	}
	
	@Override
	public MapEntryApi mapReference(String name, Class<?> collectionType, Class<?> projectionType) {
		return projectionBuilder.mapReference(name, collectionType, projectionType);
	}
	
	@Override
	public MapEntryApi mapReference(String name, Class<?> collectionType, String projectionName) {
		return projectionBuilder.mapReference(name,  collectionType, projectionName);
	}

	@Override
	public Projection<Map<String, Object>> build(ProjectionRegistry registry) throws ProjectionBuilderError {
		return projectionBuilder.build(registry);
	}
}