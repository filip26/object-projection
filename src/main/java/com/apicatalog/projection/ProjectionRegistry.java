package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.annotation.mapper.ProjectionMapper;

public class ProjectionRegistry {

	final ProjectionMapper mapper;
	
	final Map<Class<?>, Projection<?>> index;
	
	protected ProjectionRegistry(final Map<Class<?>, Projection<?>> index) {
		this.index = index;
		this.mapper = new ProjectionMapper(this);
	}

	public static final ProjectionRegistry newInstance() {
		return new ProjectionRegistry(new LinkedHashMap<>());
	}
	
	@SuppressWarnings("unchecked")
	public <P> Projection<P> get(final Class<P> projectionClass) {
		return (Projection<P>) index.get(projectionClass);
	}

	public <P> P compose(Class<P> projectionClass, Object...values) throws ProjectionError {
		return Optional.ofNullable(get(projectionClass))
					.orElseThrow(() -> unknownProjection(projectionClass))
					.compose(values);					
	}
			
	@SuppressWarnings("unchecked")
	public void extract(Object projection, Object...objects) throws ProjectionError {
		Optional.ofNullable(get((Class<Object>)projection.getClass()))
				.orElseThrow(() -> unknownProjection(projection.getClass()))
				.extract(projection, objects);
	}	

	public <S> S extract(Object projection, Class<S> objectClass) throws ProjectionError {
		return extract(projection, objectClass, null);
	}
	
	@SuppressWarnings("unchecked")
	public <S> S extract(Object projection, Class<S> objectClass, String qualifier) throws ProjectionError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
				.orElseThrow(() -> unknownProjection(projection.getClass()))
				.extract(projection, objectClass, qualifier);
	}	

	public ProjectionRegistry register(final Projection<?> projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		index.put(projection.getProjectionClass(), projection);
		return this;
	}

	public ProjectionRegistry register(Class<?> annotatedProjectionClass) {
		if (annotatedProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		Optional.ofNullable(mapper.getProjectionOf(annotatedProjectionClass)).ifPresent(this::register);		
		return this;
	}

	public ProjectionMapper getMapper() {
		return mapper;
	}
	
	ProjectionError unknownProjection(Class<?> projectionClass) {
		return new ProjectionError("Projection " + projectionClass.getCanonicalName() + " is not present.");
	}
	
}