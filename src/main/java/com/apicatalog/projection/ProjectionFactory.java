package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.converter.ConvertorError;
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
	public Object[] decompose(Object projection) throws ProjectionError, ConvertorError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
					.orElseThrow(() -> new ProjectionError("The projection for " + projection.getClass().getCanonicalName() + " is not present."))
					.decompose(projection);
	}

	public <P> P compose(Class<P> projectionClass, Object...values) throws ProjectionError, ConvertorError {
		return Optional.ofNullable(get(projectionClass))
					.orElseThrow(() -> new ProjectionError("The projection for " + projectionClass.getCanonicalName() + " is not present."))
					.compose(values);					
	}
}