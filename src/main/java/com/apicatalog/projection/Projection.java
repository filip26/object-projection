package com.apicatalog.projection;

import java.util.Collection;

import com.apicatalog.projection.api.BuilderApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.api.map.impl.MapProjectionApiImpl;
import com.apicatalog.projection.api.object.ObjectProjectionApi;
import com.apicatalog.projection.api.object.impl.ProjectionApiImpl;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;

public interface Projection<P> {
	
	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	P compose(Object... objects) throws ProjectionError;
	
	P compose(ProjectionStack stack, CompositionContext context) throws ProjectionError;
		

	/**
	 * Extract exact source value for the given projection
	 *
	 */
	<S> S extract(P projection, Class<S> objectType) throws ProjectionError;
	
	<S> S extract(P projection, String qualifier, Class<S> objectType) throws ProjectionError;

	<I> Collection<I> extractCollection(P projection, Class<I> componentType) throws ProjectionError;
	
	<I> Collection<I> extractCollection(P projection, String qualifier, Class<I> componentType) throws ProjectionError;

	void extract(P projection, ExtractionContext context) throws ProjectionError;
	
	String getName();

	static <P> ObjectProjectionApi<P> bind(Class<P> projectionType) {
		return ProjectionApiImpl.bind(projectionType);
	}
	
	static <P> BuilderApi<P> scan(final Class<P> projectionType) {
		return new BuilderApi<P>() {
			@Override
			public Projection<P> build(ProjectionRegistry registry) throws ProjectionBuilderError {

				Projection<P> projection = registry.getMapper().getProjectionOf(projectionType);
				
				registry.register(projection);
				
				return projection;
			}
		};
	}

	static MapProjectionApi hashMap(final String name) {
		return MapProjectionApiImpl.hashMap(name);
	}	
}
