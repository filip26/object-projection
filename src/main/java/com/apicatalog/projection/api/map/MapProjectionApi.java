package com.apicatalog.projection.api.map;

import java.util.Map;

import com.apicatalog.projection.api.BuilderApi;

public interface MapProjectionApi extends BuilderApi<Map<String, Object>> {

	MapEntryApi map(String name, Class<?> type);
	
	MapEntryApi map(String name, Class<?> type, Class<?> componentType);
	
}
