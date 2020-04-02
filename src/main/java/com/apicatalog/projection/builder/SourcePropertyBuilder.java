package com.apicatalog.projection.builder;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.Converter;

public class SourcePropertyBuilder<P> {
	
	ProjectionBuilder<P> projectionBuilder;
	
	protected SourcePropertyBuilder(ProjectionBuilder<P> projection) {
		this.projectionBuilder = projection;
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
		return projectionBuilder.map(propertyName);
	}

	
	public Projection<P> build(TypeAdapters typeAdapters) {
		return projectionBuilder.build(typeAdapters);
	}


	public SourcePropertyBuilder<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {

		return this;
	}



	public SourcesPropertyBuilder<P> source(Class<?> sourceClass, String sourceProperty) {

		
		return new SourcesPropertyBuilder<>(projectionBuilder);
	}

	public SourcesPropertyBuilder<P> source(Class<?> sourceClass) {

		
		return new SourcesPropertyBuilder<>(projectionBuilder);
	}	
}
