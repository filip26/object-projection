package com.apicatalog.projection.api.map;

public interface MapProjectionApi {

	MapEntryApi mapString(String propertyName);
	
	MapEntryApi mapInteger(String propertyName);
	
	MapEntryApi mapLong(String propertyName);

	MapEntryApi mapFloat(String propertyName);
	
	MapEntryApi mapDouble(String propertyName);
	
	MapEntryApi mapBoolean(String propertyName);
	
	MapEntryApi mapDate(String propertyName);
	
	MapEntryApi mapInstant(String propertyName);

	MapEntryApi mapObject(String propertyName, Class<?> objectType);
	
	MapEntryApi mapCollection(String propertyName, Class<?> collectionType, Class<?> componentType);
	
	MapEntryApi mapReference(String propertyName, Class<?> projectionType);
	
	MapEntryApi mapReference(String propertyName, String projectionName);
	
	MapEntryApi mapReference(String propertyName, Class<?> collectionType, Class<?> projectionType);
	
	MapEntryApi mapReference(String propertyName, Class<?> collectionType, String projectionName);

}
