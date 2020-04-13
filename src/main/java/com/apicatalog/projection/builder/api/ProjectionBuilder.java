package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.List;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public class ProjectionBuilder<P> {
	
	final Class<P> projectionClass;
	
	final List<MappedPropertyBuilderApi<P>> propertyBuilders;
	
	protected ProjectionBuilder(Class<P> projectionClass) {
		this.projectionClass = projectionClass;
		this.propertyBuilders = new ArrayList<>();
	}
	
	public static final <T> ProjectionBuilder<T> bind(Class<T> projectionClass) {
		return new ProjectionBuilder<>(projectionClass);
	}

	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return map(propertyName, false);
	}

	public MappedPropertyBuilderApi<P> map(String propertyName, boolean reference) {
		
		final MappedPropertyBuilderApi<P> propertyBuilder = 
					new MappedPropertyBuilderApi<>(this, propertyName, reference);
		propertyBuilders.add(propertyBuilder);
		return propertyBuilder;
	}
	
	public Projection<P> build(ProjectionRegistry factory) throws ProjectionError {

		final List<PropertyReader> readers = new ArrayList<>(); 
		final List<PropertyWriter> writers = new ArrayList<>();
		
		for (final MappedPropertyBuilderApi<P> propertyBuilder : propertyBuilders) {
			propertyBuilder
					.buildPropertyReader(factory)
					.ifPresent(readers::add);
			
			propertyBuilder
					.buildPropertyWriter(factory)
					.ifPresent(writers::add);
		}
		
		if (readers.isEmpty() && writers.isEmpty()) {
			return null;
		}
		
		final Projection<P> projection = Projection.newInstance(projectionClass, readers.toArray(new PropertyReader[0]), writers.toArray(new PropertyWriter[0]));

		factory.register(projection);
		
		return projection;
	}

	public Class<?> projectionClass() {
		return projectionClass;
	}
}
