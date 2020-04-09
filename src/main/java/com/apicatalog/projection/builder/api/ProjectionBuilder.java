package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.List;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
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
		final MappedPropertyBuilderApi<P> propertyBuilder = new MappedPropertyBuilderApi<>(this, new TypeConversions(), propertyName, reference); 	//FIXME remove new Implici
		propertyBuilders.add(propertyBuilder);
		return propertyBuilder;
	}
	
	public Projection<P> build(ProjectionRegistry factory, TypeAdaptersLegacy typeAdapters) throws ProjectionError {

		final List<ProjectionProperty> properties = new ArrayList<>(); 
		
		for (final MappedPropertyBuilderApi<P> propertyBuilder : propertyBuilders) {
			propertyBuilder
					.buildProperty(factory, typeAdapters)
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
