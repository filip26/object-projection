package com.apicatalog.projection.context;

import java.util.Objects;

final class ProjectionStackKey {

	final Object projection;
	final Class<?> projectionClass;
	
	protected ProjectionStackKey(Object projection, Class<?> projectionClass) {
		this.projection = projection;
		this.projectionClass = projectionClass;
	}
	
	public static final ProjectionStackKey of(Object projection) {
		return new ProjectionStackKey(projection, projection.getClass());
	}
	
	protected static final ProjectionStackKey of(Class<?> clazz) {
		return new ProjectionStackKey(null, clazz);
	}

	public Object getProjection() {
		return projection;
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectionClass);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProjectionStackKey other = (ProjectionStackKey) obj;
		return Objects.equals(projectionClass, other.projectionClass);
	}
}
