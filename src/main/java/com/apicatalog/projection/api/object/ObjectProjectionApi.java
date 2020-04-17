package com.apicatalog.projection.api.object;

import com.apicatalog.projection.api.BuilderApi;

public interface ObjectProjectionApi<P> extends BuilderApi<P> {

	ObjectPropertyApi<P> map(String propertyName);

	ObjectPropertyApi<P> map(String propertyName, boolean reference);
	
}
