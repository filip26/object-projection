package com.apicatalog.projection.builder;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.adapter.TypeAdapters;

public class PropertyBuilder<P> {
	
	ProjectionBuilder<P> projectionBuilder;
	
	public PropertyBuilder(ProjectionBuilder<P> projection) {
		this.projectionBuilder = projection;
	}
	
	public SourcePropertyBuilder<P> source(String sourceProperty, Class<?> sourceClass) {

		
		return new SourcePropertyBuilder<>(projectionBuilder);
	}

	public SourcePropertyBuilder<P> source(Class<?> sourceClass) {

		
		return new SourcePropertyBuilder<>(projectionBuilder);
	}

	
	public Projection<P> build(TypeAdapters typeAdapters) {
		return projectionBuilder.build(typeAdapters);
	}


	public ProvidedPropertyBuilder<P> provided() {

		return new ProvidedPropertyBuilder<>(projectionBuilder);
	}
	
}
