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
	public MapEntryApi mapDate(String propertyName) {
		return projectionBuilder.mapDate(propertyName);
	}

	@Override
	public MapEntryApi mapInstant(String propertyName) {
		return projectionBuilder.mapInstant(propertyName);
	}
	
	@Override
	public MapEntryApi map(String name, Class<?> objectType) {
		return projectionBuilder.map(name, objectType);
	}
	
	@Override
	public MapEntryApi mapCollection(String name, Class<?> collectionType, Class<?> componentType) {
		return projectionBuilder.mapCollection(name, collectionType, componentType);
	}
	
	@Override
	public MapEntryApi ref(String name, Class<?> projectionType) {
		return projectionBuilder.ref(name, projectionType);
	}
	
	@Override
	public MapEntryApi ref(String name, String projectionName) {
		return projectionBuilder.ref(name, projectionName);
	}
	
	@Override
	public MapEntryApi refArray(String propertyName, Class<?> projectionType) {
		return projectionBuilder.refArray(propertyName, projectionType);
	}

	@Override
	public MapEntryApi refArray(String propertyName, String projectionName) {
		return projectionBuilder.refArray(propertyName, projectionName);
	}
	
	@Override
	public MapEntryApi refCollection(String name, Class<?> collectionType, Class<?> projectionType) {
		return projectionBuilder.refCollection(name, collectionType, projectionType);
	}
	
	@Override
	public MapEntryApi refCollection(String name, Class<?> collectionType, String projectionName) {
		return projectionBuilder.refCollection(name,  collectionType, projectionName);
	}
}