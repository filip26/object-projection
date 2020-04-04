package com.apicatalog.projection.builder.api;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.ProvidedPropertyBuilder;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;

public class ProvidedPropertyBuilderApi<P> {
	
	ProjectionBuilder<P> projectionBuilder;

	ProvidedPropertyBuilder providedPropertyBuilder;
	
	protected ProvidedPropertyBuilderApi(ProjectionBuilder<P> projection) {
		this.projectionBuilder = projection;
		this.providedPropertyBuilder = ProvidedPropertyBuilder.newInstance();
	}
	
	public ProvidedPropertyBuilderApi<P> optional() {
		providedPropertyBuilder.optional(true);
		return this;
	}

	public ProvidedPropertyBuilderApi<P> required() {
		providedPropertyBuilder.optional(false);
		return this;
	}


	public ProvidedPropertyBuilderApi<P> qualifier(String qualifier) {
		providedPropertyBuilder.qualifier(qualifier);
		return this;
	}
	
	protected ProvidedPropertyBuilderApi<P> targetGetter(Getter targetGetter) {
		providedPropertyBuilder = providedPropertyBuilder.targetGetter(targetGetter);
		return this;
	}

	protected ProvidedPropertyBuilderApi<P> targetSetter(Setter targetSetter) {
		providedPropertyBuilder = providedPropertyBuilder.targetSetter(targetSetter);
		return this;
	}
	
	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	public Projection<P> build(ProjectionRegistry factory, TypeAdapters typeAdapters) throws ProjectionError {
		return projectionBuilder.build(factory, typeAdapters);
	}	
	
	protected ProjectionProperty buildProperty(ProjectionRegistry factory, TypeAdapters typeAdapters) {
		return providedPropertyBuilder.build(factory, typeAdapters);
	}
}
