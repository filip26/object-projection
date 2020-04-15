package com.apicatalog.projection.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.PropertyApi;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ProjectionBuilder<P> implements ProjectionApi<P> {
	
	final Class<P> projectionClass;
	
	final List<PropertyApiImpl<P>> properties;
	
	protected ProjectionBuilder(final Class<P> projectionClass) {
		this.projectionClass = projectionClass;
		this.properties = new ArrayList<>();
	}
	
	public static final <T> ProjectionApi<T> bind(final Class<T> projectionClass) {
		return new ProjectionBuilder<>(projectionClass);
	}

	@Override
	public PropertyApi<P> map(final String propertyName) {
		return map(propertyName, false);
	}

	@Override
	public PropertyApi<P> map(final String propertyName, final boolean reference) {
		
		final PropertyApiImpl<P> propertyBuilder = new PropertyApiImpl<>(this, propertyName, reference);
		properties.add(propertyBuilder);
		
		return propertyBuilder;
	}
	
	@Override
	public Projection<P> build(final ProjectionRegistry factory) throws ProjectionBuilderError {

		final List<PropertyReader> readers = new ArrayList<>(); 
		final List<PropertyWriter> writers = new ArrayList<>();
		
		for (final PropertyApiImpl<P> propertyBuilder : properties) {
			propertyBuilder
					.buildReader(factory)
					.ifPresent(readers::add);
			
			propertyBuilder
					.buildWriter(factory)
					.ifPresent(writers::add);
		}
		
		if (readers.isEmpty() && writers.isEmpty()) {
			return null;
		}
		
		final Projection<P> projection = 
					Projection.newInstance(
									projectionClass, 
									readers.toArray(new PropertyReader[0]), 
									writers.toArray(new PropertyWriter[0])
									);

		factory.register(projection);
		
		return projection;
	}

	protected Class<?> projectionClass() {
		return projectionClass;
	}
}