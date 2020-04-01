package com.apicatalog.projection.objects;

import java.util.Objects;

class ProjectionQueueKey {

	final Object projection;
	final Class<?> projectionClass;
	
	protected ProjectionQueueKey(Object projection, Class<?> projectionClass) {
		this.projection = projection;
		this.projectionClass = projectionClass;
	}
	
	public static final ProjectionQueueKey of(Object projection) {
		return new ProjectionQueueKey(projection, projection.getClass());
	}
	
	protected static final ProjectionQueueKey of(Class<?> clazz) {
		return new ProjectionQueueKey(null, clazz);
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
		ProjectionQueueKey other = (ProjectionQueueKey) obj;
		return Objects.equals(projectionClass, other.projectionClass);
	}
}
