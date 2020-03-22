package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;

public class MetaProjectionIndex {

	final Map<Class<?>, MetaProjection> projections;
	
	public MetaProjectionIndex() {
		this(new LinkedHashMap<>());
	}
	
	public MetaProjectionIndex(final Map<Class<?>, MetaProjection> projections) {
		this.projections = projections;
	}

	
	public void add(final MetaProjection projection) {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		
		projections.put(projection.getProjectionClass(), projection);
		
	}

	public MetaProjection get(final Class<?> projectionClass) {
		return projections.get(projectionClass);
	}
}