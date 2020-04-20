package com.apicatalog.projection;

import java.util.Collection;

import com.apicatalog.projection.api.BuilderApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.api.map.impl.MapProjectionApiImpl;
import com.apicatalog.projection.api.object.ObjectProjectionApi;
import com.apicatalog.projection.api.object.impl.ProjectionApiImpl;

public interface Projection<P> {

	P compose(Object... objects) throws ProjectionError;
	
	<S> S extract(P projection, Class<S> objectType) throws ProjectionError;
	
	<S> S extract(P projection, String qualifier, Class<S> objectType) throws ProjectionError;
	
	<I> Collection<I> extractCollection(P projection, Class<I> componentType) throws ProjectionError;
	
	<I> Collection<I> extractCollection(P projection, String qualifier, Class<I> componentType) throws ProjectionError;

	String getName();
	
	Class<?> getType();
	
	ProjectionComposer<P> getComposer();
	
	ProjectionExtractor<P> getExtractor();
	
	static <P> ObjectProjectionApi<P> bind(Class<P> projectionType) {
		return ProjectionApiImpl.bind(projectionType);
	}
	
	static MapProjectionApi hashMap() {
		return hashMap(null);
	}
	
	static MapProjectionApi hashMap(final String name) {
		return MapProjectionApiImpl.hashMap(name);
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
}
