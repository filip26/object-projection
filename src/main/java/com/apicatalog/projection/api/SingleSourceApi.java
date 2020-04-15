package com.apicatalog.projection.api;

public interface SingleSourceApi<P> extends ProjectionApi<P>, ConversionApi<SingleSourceApi<P>> {

	SingleSourceApi<P> optional();
	SingleSourceApi<P> required();
	
	SingleSourceApi<P> readOnly();
	SingleSourceApi<P> readWrite();
	SingleSourceApi<P> writeOnly();
}
