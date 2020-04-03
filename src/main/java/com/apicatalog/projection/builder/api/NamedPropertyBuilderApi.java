package com.apicatalog.projection.builder.api;

import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.PropertyBuilder;
import com.apicatalog.projection.property.ProjectionProperty;

public class NamedPropertyBuilderApi<P> implements PropertyBuilder {
	
	final ProjectionBuilder<P> projectionBuilder;
	final String propertyName;
	final boolean reference;
	
	PropertyBuilder propertyBuilder;
	
	public NamedPropertyBuilderApi(ProjectionBuilder<P> projection, String propertyName, boolean reference) {
		this.projectionBuilder = projection;
		this.propertyName = propertyName;
		this.reference = reference;
	}
	
	public SourcePropertyBuilderApi<P> source(Class<?> sourceClass) {
		return source(sourceClass, propertyName);
	}

	public SourcePropertyBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {

		SourcePropertyBuilderApi<P> builder = new SourcePropertyBuilderApi<>(projectionBuilder, propertyName, reference, sourceClass, sourceProperty);
		this.propertyBuilder = builder;
		return builder;
	}
	
	public Projection<P> build(ProjectionFactory factory, TypeAdapters typeAdapters) {
		return projectionBuilder.build(factory, typeAdapters);
	}

	public ProvidedPropertyBuilderApi<P> provided() {

		return new ProvidedPropertyBuilderApi<>(projectionBuilder);
	}

	public ProjectionBuilder<P> constant(String string) {
		// TODO Auto-generated method stub
		return projectionBuilder;
	}

	public NamedPropertyBuilderApi<P> sources() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ProjectionProperty getProperty(ProjectionFactory factory, TypeAdapters typeAdapters) {
		return Optional.ofNullable(propertyBuilder).map(b -> b.getProperty(factory, typeAdapters)).orElse(null);
	}
	
}
