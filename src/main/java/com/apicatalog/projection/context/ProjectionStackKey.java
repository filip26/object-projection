package com.apicatalog.projection.context;

import java.util.Objects;

final class ProjectionStackKey {

	final Object projection;
	final String projectionName;
	
	protected ProjectionStackKey(final Object projection, final String projectionName) {
		this.projection = projection;
		this.projectionName = projectionName;
	}
	
	public static final ProjectionStackKey of(final String name, final Object projection) {
		return new ProjectionStackKey(projection, name);
	}
	
	protected static final ProjectionStackKey of(final String name) {
		return new ProjectionStackKey(null, name);
	}

	public Object getProjection() {
		return projection;
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectionName);
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
		return Objects.equals(projectionName, other.projectionName);
	}
}
