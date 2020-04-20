package com.apicatalog.projection.api.map;

import java.util.Map;

import com.apicatalog.projection.api.BuilderApi;

public interface MapProjectionApi extends BuilderApi<Map<String, Object>> {

	MapEntryApi mapString(String name);
	
	MapEntryApi mapInteger(String name);
	
	MapEntryApi mapLong(String name);

	MapEntryApi mapFloat(String name);
	
	MapEntryApi mapDouble(String name);
	
	MapEntryApi mapBoolean(String name);
	
	MapEntryApi mapObject(String name, Class<?> objectType);
	
	MapEntryApi mapCollection(String name, Class<?> collectionType, Class<?> componentType);
	
	MapEntryApi mapReference(String name, Class<?> projectionType);
	
	MapEntryApi mapReference(String name, String projectionName);
	
	MapEntryApi mapReference(String name, Class<?> collectionType, Class<?> projectionType);
	
	MapEntryApi mapReference(String name, Class<?> collectionType, String projectionName);

}
