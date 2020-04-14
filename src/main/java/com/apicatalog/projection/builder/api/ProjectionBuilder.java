package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.List;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ProjectionBuilder<P> {
	
	final Class<P> projectionClass;
	
	final List<PropertyApi<P>> propertyBuilders;
	
	protected ProjectionBuilder(final Class<P> projectionClass) {
		this.projectionClass = projectionClass;
		this.propertyBuilders = new ArrayList<>();
	}
	
	public static final <T> ProjectionBuilder<T> bind(final Class<T> projectionClass) {
		return new ProjectionBuilder<>(projectionClass);
	}

	public PropertyApi<P> map(final String propertyName) {
		return map(propertyName, false);
	}

	public PropertyApi<P> map(final String propertyName, final boolean reference) {
		
		final PropertyApi<P> propertyBuilder = new PropertyApi<>(this, propertyName, reference);
		propertyBuilders.add(propertyBuilder);
		
		return propertyBuilder;
	}
	
	public Projection<P> build(final ProjectionRegistry factory) throws ProjectionError {

		final List<PropertyReader> readers = new ArrayList<>(); 
		final List<PropertyWriter> writers = new ArrayList<>();
		
		for (final PropertyApi<P> propertyBuilder : propertyBuilders) {
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