package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.mapper.ProjectionMapper;

public class ProjectionFactory {

	final ProjectionMapper mapper;
	
	final Map<Class<?>, Projection<?>> index;
	
	protected ProjectionFactory(final Map<Class<?>, Projection<?>> projections) {
		this.index = projections;
		this.mapper = new ProjectionMapper(this);
	}

	public static final ProjectionFactory newInstance() {
		return new ProjectionFactory(new LinkedHashMap<>());
	}
	
	@SuppressWarnings("unchecked")
	public <P> Projection<P> get(final Class<P> projectionClass) {
		return (Projection<P>) index.get(projectionClass);
	}

	@SuppressWarnings("unchecked")
	public Object[] decompose(Object projection) throws ProjectionError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
					.orElseThrow(() -> unknownProjection(projection.getClass()))
					.decompose(projection);
	}

	public <P> P compose(Class<P> projectionClass, Object...values) throws ProjectionError {
		return Optional.ofNullable(get(projectionClass))
					.orElseThrow(() -> unknownProjection(projectionClass))
					.compose(values);					
	}
	
	public <P> P extract(Class<P> sourceObjectClass, Object projection) throws ProjectionError {
		return extract(sourceObjectClass, null, projection);
	}
	
	@SuppressWarnings("unchecked")
	public <P> P extract(final Class<P> sourceObjectClass, final String qualifier, final Object projection) throws ProjectionError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
				.orElseThrow(() -> unknownProjection(projection.getClass()))
				.extract(sourceObjectClass, qualifier, projection);		
	}

	public ProjectionFactory add(final Projection<?> projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		index.put(projection.getProjectionClass(), projection);
		return this;
	}

	public ProjectionFactory add(Class<?> projectionClass) {
		Optional.ofNullable(mapper.getProjection(projectionClass)).ifPresent(this::add);		
		return this;
	}

	public ProjectionMapper getMapper() {
		return mapper;
	}
	
	ProjectionError unknownProjection(Class<?> projectionClass) {
		return new ProjectionError("Projection " + projectionClass.getCanonicalName() + " is not present.");
	}
	
}