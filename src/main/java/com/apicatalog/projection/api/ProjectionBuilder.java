package com.apicatalog.projection.api;

import com.apicatalog.projection.api.impl.ProjectionBuilderImpl;

public interface ProjectionBuilder {

	static <P> ProjectionApi<P> bind(Class<P> projection) {
		return ProjectionBuilderImpl.bind(projection);
	}
	
}
