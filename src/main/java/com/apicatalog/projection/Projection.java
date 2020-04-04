package com.apicatalog.projection;

import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.property.ProjectionProperty;

public interface Projection<P> {

	Class<P> getProjectionClass();
	
	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	P compose(Object...objects) throws ProjectionError;
	
	P compose(ProjectionQueue queue, ContextObjects context) throws ProjectionError;
	
	/**
	 * Decompose a projection into a source of values
	 * 
	 * @param projection to decompose
	 * @return objects extracted from the projection
	 * @throws ProjectionError
	 */
	Object[] decompose(P projection) throws ProjectionError;
	
	Object[] decompose(P projection, ContextObjects context) throws ProjectionError;

	/**
	 * Extract exact source value for the given projection
	 * 
	 * @param sourceClass
	 * @param qualifier
	 * @param projection
	 * @return
	 * @throws ProjectionError 
	 */
	<S> S extract(Class<S> sourceClass, String qualifier, P projection) throws ProjectionError;

	ProjectionProperty[] getProperties();
}
