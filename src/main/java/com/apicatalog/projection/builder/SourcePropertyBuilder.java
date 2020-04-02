package com.apicatalog.projection.builder;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.Converter;

public class SourcePropertyBuilder<P> {
	
	ProjectionBuilder<P> projection;
	
	protected SourcePropertyBuilder(ProjectionBuilder<P> projection) {
		this.projection = projection;
	}
	

	public SourcePropertyBuilder<P> optional() {

		
		return this;
	}

	public SourcePropertyBuilder<P> required() {

		
		return this;
	}


	public SourcePropertyBuilder<P> qualifier(String qualifier) {

		
		return this;
	}
	
	public PropertyBuilder<P> map(String propertyName) {
		return projection.map(propertyName);
	}

	
	public Projection<P> build(TypeAdapters typeAdapters) {
		return projection.build(typeAdapters);
	}


	public SourcePropertyBuilder<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {

		return this;
	}
	
}
