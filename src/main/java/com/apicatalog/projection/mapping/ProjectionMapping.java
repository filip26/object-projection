package com.apicatalog.projection.mapping;

import java.util.Collection;

import com.apicatalog.projection.ProjectionError;

public interface ProjectionMapping<P> {

	Collection<PropertyMapping> getProperties();
	
	Class<P> getProjectionClass();
	
	/**
	 * Compose a projection from the given source values
	 * 
	 * @param values values used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	P compose(Object...values) throws ProjectionError;
	
	/**
	 * Decompose a projection into source values
	 * 
	 * @param projection a projection to decompose
	 * @return values extracted from the projection
	 * @throws ProjectionError
	 */
	Object[] decompose(P projection) throws ProjectionError;
}
