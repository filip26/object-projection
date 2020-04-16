package com.apicatalog.projection.api;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;

public interface BuilderApi<P> {

	Projection<P> build(ProjectionRegistry registry) throws ProjectionBuilderError;
}
