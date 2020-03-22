package com.apicatalog.projection.mapping;

import java.util.ArrayList;
import java.util.Collection;

public class ProjectionMapping {

	final Class<?> projectionClass;
	
	final Collection<PropertyMapping> properties;

	public ProjectionMapping(Class<?> projectionClass) {
		this.projectionClass = projectionClass;
		this.properties = new ArrayList<>();
	}

	public Collection<PropertyMapping> getProperties() {
		return properties;
	}

	public Class<?> getProjectionClass() {
		return projectionClass;
	}

	public void add(PropertyMapping property) {
		this.properties.add(property);
	}
}
