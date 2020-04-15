package com.apicatalog.projection.api;

public interface ProvidedApi<P> extends ProjectionApi<P> {

	ProjectionApi<P> optional();
	ProjectionApi<P> required();

}
