package com.apicatalog.projection.builder.api;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.std.UriTemplate;

public class SourcesPropertyBuilderApi<P> {
	
	ProjectionBuilder<P> projectionBuilder;
	
	protected SourcesPropertyBuilderApi(ProjectionBuilder<P> projection) {
		this.projectionBuilder = projection;
	}
	

	public SourcesPropertyBuilderApi<P> optional() {

		
		return this;
	}

	public SourcesPropertyBuilderApi<P> required() {

		
		return this;
	}

	public SourcesPropertyBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {

		
		return new SourcesPropertyBuilderApi<>(projectionBuilder);
	}

	public SourcesPropertyBuilderApi<P> source(Class<?> sourceClass) {

		
		return new SourcesPropertyBuilderApi<>(projectionBuilder);
	}

	public SourcesPropertyBuilderApi<P> qualifier(String qualifier) {

		
		return this;
	}
	
	public NamedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	
	public Projection<P> build(ProjectionFactory factory, TypeAdapters typeAdapters) throws ProjectionError {
		return projectionBuilder.build(factory, typeAdapters);
	}


	public SourcesPropertyBuilderApi<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {

		return this;
	}


	public SourcesPropertyBuilderApi<P> reduce(Class<UriTemplate> class1, String string) {

		return this;
	}
	
}
