package com.apicatalog.projection.api;

public interface SingleSourceApi<P> extends ProjectionApi<P>, ConversionApi<SingleSourceApi<P>>, OptionalApi<SingleSourceApi<P>> {

	SingleSourceApi<P> readOnly();
	SingleSourceApi<P> readWrite();
	SingleSourceApi<P> writeOnly();
}
