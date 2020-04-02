package com.apicatalog.projection.builder;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.std.UriTemplate;

public class SourcesPropertyBuilder<P> {
	
	ProjectionBuilder<P> projectionBuilder;
	
	protected SourcesPropertyBuilder(ProjectionBuilder<P> projection) {
		this.projectionBuilder = projection;
	}
	

	public SourcesPropertyBuilder<P> optional() {

		
		return this;
	}

	public SourcesPropertyBuilder<P> required() {

		
		return this;
	}

	public SourcesPropertyBuilder<P> source(Class<?> sourceClass, String sourceProperty) {

		
		return new SourcesPropertyBuilder<>(projectionBuilder);
	}

	public SourcesPropertyBuilder<P> source(Class<?> sourceClass) {

		
		return new SourcesPropertyBuilder<>(projectionBuilder);
	}

	public SourcesPropertyBuilder<P> qualifier(String qualifier) {

		
		return this;
	}
	
	public PropertyBuilder<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	
	public Projection<P> build(TypeAdapters typeAdapters) {
		return projectionBuilder.build(typeAdapters);
	}


	public SourcesPropertyBuilder<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {

		return this;
	}


	public SourcesPropertyBuilder<P> reduce(Class<UriTemplate> class1, String string) {

		return this;
	}
	
}
