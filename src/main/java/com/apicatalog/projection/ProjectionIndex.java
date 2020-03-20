package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectionIndex {

	final Map<Class<?>, Projection> projections;
	
	public ProjectionIndex() {
		this(new LinkedHashMap<>());
	}
	
	public ProjectionIndex(final Map<Class<?>, Projection> projections) {
		this.projections = projections;
	}

	
	public void add(final Projection projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		
		projections.put(projection.getProjectionClass(), projection);
		
	}

	public Projection get(final Class<?> projectionClass) {
		return projections.get(projectionClass);
	}
}