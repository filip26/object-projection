package com.apicatalog.projection;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.PropertyBuilder;

public class ProjectionBuilder<P> {
	
	final Class<P> projectionClass;
	
	protected ProjectionBuilder(Class<P> projectionClass) {
		this.projectionClass = projectionClass;
	}
	
	public static final <T> ProjectionBuilder<T> of(Class<T> projectionClass) {
		return new ProjectionBuilder<>(projectionClass);
	}

	public PropertyBuilder<P> map(String propertyName) {

		return new PropertyBuilder<>(this);
	}

	public PropertyBuilder<P> ref(String propertyName) {
		
		return new PropertyBuilder<>(this);
	}
	
	public Projection<P> build(TypeAdapters typeAdapters) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
