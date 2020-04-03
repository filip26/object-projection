package com.apicatalog.projection.builder;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.property.ProjectionProperty;

public interface PropertyBuilder {
		
	ProjectionProperty getProperty(ProjectionFactory factory, TypeAdapters typeAdapters);
	
}
