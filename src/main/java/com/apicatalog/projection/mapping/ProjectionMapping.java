package com.apicatalog.projection.mapping;

import java.util.Collection;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;

public interface ProjectionMapping<P> {

	Collection<PropertyMapping> getProperties();
	
	Class<P> getProjectionClass();
	
	/**
	 * Compose a projection from the given source values
	 * 
	 * @param values values used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 * @throws ConverterError
	 */
	P compose(Object...values) throws ProjectionError, ConverterError;
	
	/**
	 * Decompose a projection into source values
	 * 
	 * @param projection a projection to decompose
	 * @return values extracted from the projection
	 * @throws ProjectionError
	 * @throws ConverterError
	 */
	Object[] decompose(P projection) throws ProjectionError, ConverterError;
}
