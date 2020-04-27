package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.apicatalog.projection.annotation.mapper.ProjectionMapper;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.conversion.TypeConversions;

public final class Registry {

	final ProjectionMapper mapper;
	final TypeConversions typeConversions;
	
	final Map<String, Projection<?>> index;
	
	final Map<String, Collection<Consumer<Projection<?>>>> consumers;
	
	protected Registry(final Map<String, Projection<?>> index) {
		this.index = index;
		this.mapper = new ProjectionMapper(this);
		this.typeConversions = new TypeConversions();
		this.consumers = new HashMap<>(48);
	}

	public static final Registry newInstance() {
		return new Registry(new LinkedHashMap<>());
	}
	
	public <P> Projection<P> get(final Class<P> projectionClass) {
		return get(projectionClass.getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	public <P> Projection<P> get(final String name) {
		return (Projection<P>) index.get(name);
	}
	
	public Registry register(final Projection<?> projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		
		final Optional<String> name = projection.getName();
		
		if (name.isPresent()) {
			index.put(name.get(), projection);
			if (consumers.containsKey(name.get())) {
				consumers.remove(name.get()).forEach(c -> c.accept(projection));
			}
		}
		
		return this;
	}

	public Registry register(final Class<?> annotatedProjectionType) throws ProjectionError {
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
	
	public void request(final String projectionName, final Consumer<Projection<?>> consumer) {

		final Projection<?> projection = index.get(projectionName);
		
		if (projection != null) {
			consumer.accept(projection);
			return;
		}
		
		consumers.computeIfAbsent(projectionName, x -> new ArrayList<>()).add(consumer);
	}
	
	@SuppressWarnings("unchecked")
	public Stream<Projection<Object>> stream() {
		return index.values().stream().map(o -> (Projection<Object>)o);
	}
	
}