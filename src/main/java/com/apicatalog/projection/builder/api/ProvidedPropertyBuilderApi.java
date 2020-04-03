package com.apicatalog.projection.builder.api;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;

public class ProvidedPropertyBuilderApi<P> {
	
	ProjectionBuilder<P> projection;
	
	protected ProvidedPropertyBuilderApi(ProjectionBuilder<P> projection) {
		this.projection = projection;
	}
	

	public ProvidedPropertyBuilderApi<P> optional() {

		
		return this;
	}

	public ProvidedPropertyBuilderApi<P> required() {

		
		return this;
	}


	public ProvidedPropertyBuilderApi<P> qualifier(String qualifier) {

		
		return this;
	}
	
	public NamedPropertyBuilderApi<P> map(String propertyName) {
		return projection.map(propertyName);
	}

	public Projection<P> build(ProjectionFactory factory, TypeAdapters typeAdapters) throws ProjectionError {
		return projection.build(factory, typeAdapters);
	}	
}
