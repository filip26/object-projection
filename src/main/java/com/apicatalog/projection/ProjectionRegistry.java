package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.annotation.mapper.ProjectionMapper;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.object.ObjectProjectionApi;
import com.apicatalog.projection.conversion.TypeConversions;

public final class ProjectionRegistry {

	final ProjectionMapper mapper;
	final TypeConversions typeConversions;
	
	final Map<String, Projection<?>> index;
	
	protected ProjectionRegistry(final Map<String, Projection<?>> index) {
		this.index = index;
		this.mapper = new ProjectionMapper(this);
		this.typeConversions = new TypeConversions();
	}

	public static final ProjectionRegistry newInstance() {
		return new ProjectionRegistry(new LinkedHashMap<>());
	}
	
	public <P> Projection<P> get(final Class<P> projectionClass) {
		return get(projectionClass.getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	public <P> Projection<P> get(final String name) {
		return (Projection<P>) index.get(name);
	}
	
	public ProjectionRegistry register(final Projection<?> projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		index.put(projection.getName(), projection);
		return this;
	}

	public ProjectionRegistry register(final ObjectProjectionApi<?> projectionApi) throws ProjectionBuilderError {
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