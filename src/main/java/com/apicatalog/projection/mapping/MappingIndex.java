package com.apicatalog.projection.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

public class MappingIndex {

	final Map<Class<?>, ProjectionMapping> projections;
	
	public MappingIndex() {
		this(new LinkedHashMap<>());
	}
	
	public MappingIndex(final Map<Class<?>, ProjectionMapping> projections) {
		this.projections = projections;
	}

	public MappingIndex add(final ProjectionMapping projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		projections.put(projection.getProjectionClass(), projection);
		return this;
	}

	public ProjectionMapping get(final Class<?> projectionClass) {
		return projections.get(projectionClass);
	}
}