package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.PropertyBuilder;
import com.apicatalog.projection.builder.api.NamedPropertyBuilderApi;
import com.apicatalog.projection.mapper.ProjectionImpl;
import com.apicatalog.projection.property.ProjectionProperty;

public class ProjectionBuilder<P> {
	
	final Class<P> projectionClass;
	
	final List<PropertyBuilder> propertyBuilders;
	
	protected ProjectionBuilder(Class<P> projectionClass) {
		this.projectionClass = projectionClass;
		this.propertyBuilders = new ArrayList<>();
	}
	
	public static final <T> ProjectionBuilder<T> bind(Class<T> projectionClass) {
		return new ProjectionBuilder<>(projectionClass);
	}

	public NamedPropertyBuilderApi<P> map(String propertyName) {
		return map(propertyName, false);
	}

	public NamedPropertyBuilderApi<P> map(String propertyName, boolean reference) {
		final NamedPropertyBuilderApi<P> propertyBuilder = new NamedPropertyBuilderApi<>(this, propertyName, reference);
		propertyBuilders.add(propertyBuilder);
		return propertyBuilder;
	}
	
	public Projection<P> build(ProjectionFactory factory, TypeAdapters typeAdapters) {

		ProjectionProperty[] properties = propertyBuilders
												.stream()
												.map(b -> b.getProperty(factory, typeAdapters))
												.filter(Objects::nonNull)
												.collect(Collectors.toList())
												.toArray(new ProjectionProperty[0])
												;
		if (properties.length == 0) {
			return null;
		}
		
		final ProjectionImpl<P> projection = new ProjectionImpl<>(projectionClass);
		projection.setProperties(properties);

		return projection;
	}

	
}
