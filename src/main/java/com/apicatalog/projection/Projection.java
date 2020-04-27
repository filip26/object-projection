package com.apicatalog.projection;

import java.util.Collection;
import java.util.Optional;

import com.apicatalog.projection.api.BuilderApi;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.api.map.impl.MapProjectionApiImpl;
import com.apicatalog.projection.api.object.ObjectProjectionApi;
import com.apicatalog.projection.api.object.impl.ProjectionApiImpl;

public interface Projection<P> {

	P compose(Object... objects) throws CompositionError;
	
	<S> Optional<S> extract(P projection, Class<S> objectType) throws ExtractionError;
	
	<S> Optional<S> extract(P projection, String qualifier, Class<S> objectType) throws ExtractionError;
	
	<I> Optional<Collection<I>> extractCollection(P projection, Class<I> componentType) throws ExtractionError;
	
	<I> Optional<Collection<I>> extractCollection(P projection, String qualifier, Class<I> componentType) throws ExtractionError;

	Optional<String> getName();
	
	Class<?> getType();
	
	Optional<ProjectionComposer<P>> getComposer();
	
	Optional<ProjectionExtractor<P>> getExtractor();
	
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
			public Projection<P> build(Registry registry) throws ProjectionError {

				Projection<P> projection = registry.getMapper().getProjectionOf(projectionType);
				
				registry.register(projection);
				
				return projection;
			}
		};
	}
}
