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

	MapEntryApi map(String propertyName, Class<?> objectType);

	MapEntryApi mapCollection(String propertyName, Class<?> collectionType, Class<?> componentType);
	
	MapEntryApi ref(String propertyName, Class<?> projectionType);
	
	MapEntryApi ref(String propertyName, String projectionName);

	MapEntryApi refArray(String propertyName, Class<?> projectionType);
	
	MapEntryApi refArray(String propertyName, String projectionName);
	
	MapEntryApi refCollection(String propertyName, Class<?> collectionType, Class<?> projectionType);
	
	MapEntryApi refCollection(String propertyName, Class<?> collectionType, String projectionName);

}
