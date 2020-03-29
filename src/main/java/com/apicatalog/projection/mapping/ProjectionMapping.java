package com.apicatalog.projection.mapping;

import java.util.Collection;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.Path;

public interface ProjectionMapping<P> {

	Collection<PropertyMapping> getProperties();
	
	Class<P> getProjectionClass();
	
	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	P compose(Object...objects) throws ProjectionError;
	
	P compose(Path path, Object...objects) throws ProjectionError;
	
	/**
	 * Decompose a projection into source values
	 * 
	 * @param projection a projection to decompose
	 * @return objects extracted from the projection
	 * @throws ProjectionError
	 */
	Object[] decompose(P projection) throws ProjectionError;
	
	Object[] decompose(Path path, P projection) throws ProjectionError;
}
