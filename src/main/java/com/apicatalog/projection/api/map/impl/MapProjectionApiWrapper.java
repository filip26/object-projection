package com.apicatalog.projection.api.map.impl;

import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;

public class MapProjectionApiWrapper implements MapProjectionApi {
	
	final MapProjectionBuilderApi projectionBuilder;
	
	protected MapProjectionApiWrapper(final MapProjectionBuilderApi projectionBuilder) {
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
}