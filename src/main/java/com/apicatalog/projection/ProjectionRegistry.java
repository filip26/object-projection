package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.apicatalog.projection.annotation.mapper.ProjectionMapper;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.object.ObjectProjectionApi;
import com.apicatalog.projection.conversion.TypeConversions;

public final class ProjectionRegistry {

	final ProjectionMapper mapper;
	final TypeConversions typeConversions;
	
	final Map<String, Projection<?>> index;
	
	final Map<String, Collection<Consumer<Projection<?>>>> consumers;
	
	protected ProjectionRegistry(final Map<String, Projection<?>> index) {
		this.index = index;
		this.mapper = new ProjectionMapper(this);
		this.typeConversions = new TypeConversions();
		this.consumers = new HashMap<>(48);
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
		
		final String projectionName = projection.getName();
		
		index.put(projectionName, projection);
		
		if (consumers.containsKey(projectionName)) {
			consumers.remove(projectionName).forEach(c -> c.accept(projection));
		}

		return this;
	}

	public ProjectionRegistry register(final ObjectProjectionApi<?> projectionApi) throws ProjectionBuilderError {
		projectionApi.build(this);
		return this;
	}

	public ProjectionRegistry register(Class<?> annotatedProjectionType) throws ProjectionBuilderError {
		if (annotatedProjectionType == null) {
			throw new IllegalArgumentException();
		}
		Optional.ofNullable(mapper.getProjectionOf(annotatedProjectionType)).ifPresent(this::register);		
		return this;
	}

	public ProjectionMapper getMapper() {
		return mapper;
	}
	
	public TypeConversions getTypeConversions() {
		return typeConversions;
	}
	
	ProjectionError unknownProjection(Class<?> projectionType) {
		return new ProjectionError("Projection " + projectionType.getCanonicalName() + " is not present.");
	}

	public void request(final String projectionName, final Consumer<Projection<?>> consumer) {

		final Projection<?> projection = index.get(projectionName);
		
		if (projection != null) {
			consumer.accept(projection);
			return;
		}
		
		consumers.computeIfAbsent(projectionName, x -> new ArrayList<>()).add(consumer);
	}	
}