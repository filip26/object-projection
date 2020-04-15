package com.apicatalog.projection.api;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;

public interface ProjectionApi<P> {

	PropertyApi<P> map(String propertyName);

	PropertyApi<P> map(String propertyName, boolean reference);
	
	Projection<P> build(ProjectionRegistry registry) throws ProjectionBuilderError;
}
