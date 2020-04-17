package com.apicatalog.projection.api.map;

import com.apicatalog.projection.api.SourceApi;

public interface MapEntryApi extends MapProjectionApi, SourceApi<MapSingleSourceApi> {

	MapArraySourceApi sources();

	MapProvidedApi provided();
	
	MapProvidedApi provided(final String name);
	
	MapProjectionApi constant(final String...values);
	
}
