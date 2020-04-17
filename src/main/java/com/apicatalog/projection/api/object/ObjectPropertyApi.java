package com.apicatalog.projection.api.object;

import com.apicatalog.projection.api.SourceApi;

public interface ObjectPropertyApi<P> extends ObjectProjectionApi<P>, SourceApi<ObjectSingleSourceApi<P>> {

	ObjectArraySourceApi<P> sources();

	ObjectProvidedApi<P> provided();
	
	ObjectProvidedApi<P> provided(final String name);
	
	ObjectProjectionApi<P> constant(final String...values);
	
}
