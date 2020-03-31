package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.mapping.ProjectionMapping;

public class ProjectionFactory {

	final Map<Class<?>, ProjectionMapping<?>> index;
	
	public ProjectionFactory() {
		this(new LinkedHashMap<>());
	}
	
	public ProjectionFactory(final Map<Class<?>, ProjectionMapping<?>> projections) {
		this.index = projections;
	}

	public ProjectionFactory add(final ProjectionMapping<?> projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		index.put(projection.getProjectionClass(), projection);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <P> ProjectionMapping<P> get(final Class<P> projectionClass) {
		return (ProjectionMapping<P>) index.get(projectionClass);
	}

	@SuppressWarnings("unchecked")
	public Object[] decompose(Object projection) throws ProjectionError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
					.orElseThrow(() -> new ProjectionError("The projection " + projection.getClass().getCanonicalName() + " is not present."))
					.decompose(projection);
	}

	public <P> P compose(Class<P> projectionClass, Object...values) throws ProjectionError {
		return Optional.ofNullable(get(projectionClass))
					.orElseThrow(() -> new ProjectionError("The projection " + projectionClass.getCanonicalName() + " is not present."))
					.compose(values);					
	}
	
	public <P> P extract(Class<P> sourceObjectClass, Object projection) throws ProjectionError {
		return extract(sourceObjectClass, null, projection);
	}
	
	@SuppressWarnings("unchecked")
	public <P> P extract(Class<P> sourceObjectClass, String qualifier, Object projection) throws ProjectionError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
				.orElseThrow(() -> new ProjectionError("The projection " + projection.getClass().getCanonicalName() + " is not present."))
				.extract(sourceObjectClass, qualifier, projection);		
	}
	
}