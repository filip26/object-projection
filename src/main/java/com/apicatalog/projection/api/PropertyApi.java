package com.apicatalog.projection.api;

public interface PropertyApi<P> extends ProjectionApi<P>, SourceApi<SingleSourceApi<P>> {

	ArraySourceApi<P> sources();

	ProvidedApi<P> provided();
	
	ProvidedApi<P> provided(final String name);
	
	ProjectionApi<P> constant(final String...values);
	
}
