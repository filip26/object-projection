package com.apicatalog.projection.builder;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.adapter.TypeAdapters;

public class ProvidedPropertyBuilder<P> {
	
	ProjectionBuilder<P> projection;
	
	protected ProvidedPropertyBuilder(ProjectionBuilder<P> projection) {
		this.projection = projection;
	}
	

	public ProvidedPropertyBuilder<P> optional() {

		
		return this;
	}

	public ProvidedPropertyBuilder<P> required() {

		
		return this;
	}


	public ProvidedPropertyBuilder<P> qualifier(String qualifier) {

		
		return this;
	}
	
	public PropertyBuilder<P> map(String propertyName) {
		return projection.map(propertyName);
	}

	
	public Projection<P> build(TypeAdapters typeAdapters) {
		return projection.build(typeAdapters);
	}	
}
