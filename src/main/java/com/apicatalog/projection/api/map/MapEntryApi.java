package com.apicatalog.projection.api.map;

import com.apicatalog.projection.api.SourceApi;

public interface MapEntryApi extends SourceApi<MapSingleSourceApi> {

	MapArraySourceApi sources();

	MapProvidedApi provided();
	
	MapProvidedApi provided(final String name);
	
	MapProjectionBuilderApi constant(final String...values);
	
}
