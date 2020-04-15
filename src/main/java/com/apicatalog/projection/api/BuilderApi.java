package com.apicatalog.projection.api;

public interface BuilderApi {

	<P> ProjectionApi<P> bind(Class<P> projection);
	
}
