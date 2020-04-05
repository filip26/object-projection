package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.property.ProjectionProperty;

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
		final MappedPropertyBuilderApi<P> propertyBuilder = new MappedPropertyBuilderApi<>(this, propertyName, reference);
		propertyBuilders.add(propertyBuilder);
		return propertyBuilder;
	}
	
	public Projection<P> build(ProjectionRegistry factory, TypeAdapters typeAdapters) throws ProjectionError {

		final List<ProjectionProperty> properties = new ArrayList<>(); 
		
		for (final MappedPropertyBuilderApi<P> propertyBuilder : propertyBuilders) {
			Optional.ofNullable(propertyBuilder.buildProperty(factory, typeAdapters))
					.ifPresent(properties::add);
		}
		
		if (properties.isEmpty()) {
			return null;
		}
		
		final Projection<P> projection = Projection.newInstance(projectionClass, properties.toArray(new ProjectionProperty[0]));

		factory.register(projection);
		
		return projection;
	}

	public Class<?> projectionClass() {
		return projectionClass;
	}
}
