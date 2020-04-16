package com.apicatalog.projection.api;

public interface ProjectionApi<P> extends BuilderApi<P> {

	PropertyApi<P> map(String propertyName);

	PropertyApi<P> map(String propertyName, boolean reference);
	
}
