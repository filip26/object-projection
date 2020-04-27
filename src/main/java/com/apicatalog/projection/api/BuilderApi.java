package com.apicatalog.projection.api;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;

public interface BuilderApi<P> {

	Projection<P> build(Registry registry) throws ProjectionError;
}
