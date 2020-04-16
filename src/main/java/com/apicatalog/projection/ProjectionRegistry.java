package com.apicatalog.projection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.annotation.mapper.ProjectionMapper;
import com.apicatalog.projection.api.ProjectionApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.conversion.TypeConversions;

public final class ProjectionRegistry {

	final ProjectionMapper mapper;
	final TypeConversions typeConversions;
	
	final Map<Class<?>, Projection<?>> index;
	
	protected ProjectionRegistry(final Map<Class<?>, Projection<?>> index) {
		this.index = index;
		this.mapper = new ProjectionMapper(this);
		this.typeConversions = new TypeConversions();
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

	public <S> S extract(Object projection, Class<S> objectType) throws ProjectionError {
		return extract(projection, null, objectType);
	}
	
	@SuppressWarnings("unchecked")
	public <S> S extract(Object projection, String qualifier, Class<S> objectType) throws ProjectionError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
					.orElseThrow(() -> unknownProjection(projection.getClass()))
					.extract(projection, qualifier, objectType);
	}	

	public <I> Collection<I> extractCollection(Object projection, Class<I> componentType) throws ProjectionError {
		return extractCollection(projection, null, componentType);
	}
	
	@SuppressWarnings("unchecked")
	public <I> Collection<I> extractCollection(Object projection, String qualifier, Class<I> componentType) throws ProjectionError {
		return Optional.ofNullable(get((Class<Object>)projection.getClass()))
					.orElseThrow(() -> unknownProjection(projection.getClass()))
					.extractCollection(projection, qualifier, componentType);
	}	
	
	public ProjectionRegistry register(final Projection<?> projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		index.put(projection.getProjectionClass(), projection);
		return this;
	}

	public ProjectionRegistry register(final ProjectionApi<?> projectionApi) throws ProjectionBuilderError {
		projectionApi.build(this);
		return this;
	}

	public ProjectionRegistry register(Class<?> annotatedProjectionClass) throws ProjectionBuilderError {
		if (annotatedProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		Optional.ofNullable(mapper.getProjectionOf(annotatedProjectionClass)).ifPresent(this::register);		
		return this;
	}

	public ProjectionMapper getMapper() {
		return mapper;
	}
	
	public TypeConversions getTypeConversions() {
		return typeConversions;
	}
	
	ProjectionError unknownProjection(Class<?> projectionClass) {
		return new ProjectionError("Projection " + projectionClass.getCanonicalName() + " is not present.");
	}
	
}