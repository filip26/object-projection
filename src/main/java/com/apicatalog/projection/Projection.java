package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.Collection;

public class Projection {

	final Class<?> projectionClass;
	
	final Collection<ProjectionProperty> properties;

	public Projection(Class<?> projectionClass) {
		this.projectionClass = projectionClass;
		this.properties = new ArrayList<>();
	}

	public Collection<ProjectionProperty> getProperties() {
		return properties;
	}

	public Class<?> getProjectionClass() {
		return projectionClass;
	}

	public void add(ProjectionProperty property) {
		this.properties.add(property);
	}
}
