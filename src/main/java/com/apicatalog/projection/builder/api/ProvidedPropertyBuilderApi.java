package com.apicatalog.projection.builder.api;

import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.ProvidedPropertyReaderBuilder;
import com.apicatalog.projection.builder.ProvidedPropertyWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public class ProvidedPropertyBuilderApi<P> {
	
	ProjectionBuilder<P> projectionBuilder;

	ProvidedPropertyReaderBuilder providedPropertyReaderBuilder;
	ProvidedPropertyWriterBuilder providedPropertyWriterBuilder;
	
	protected ProvidedPropertyBuilderApi(ProjectionBuilder<P> projection) {
		this.projectionBuilder = projection;
		this.providedPropertyReaderBuilder = ProvidedPropertyReaderBuilder.newInstance();
		this.providedPropertyWriterBuilder = ProvidedPropertyWriterBuilder.newInstance();
	}
	
	public ProvidedPropertyBuilderApi<P> optional() {
		providedPropertyReaderBuilder.optional(true);
		providedPropertyWriterBuilder.optional(true);
		return this;
	}

	public ProvidedPropertyBuilderApi<P> required() {
		providedPropertyReaderBuilder.optional(false);
		providedPropertyWriterBuilder.optional(false);
		return this;
	}

	public ProvidedPropertyBuilderApi<P> qualifier(String qualifier) {
		providedPropertyReaderBuilder.qualifier(qualifier);
		providedPropertyWriterBuilder.qualifier(qualifier);
		return this;
	}
	
	protected ProvidedPropertyBuilderApi<P> targetGetter(Getter targetGetter) {
//		providedPropertyBuilder = providedPropertyBuilder.targetGetter(targetGetter);
		return this;
	}

	protected ProvidedPropertyBuilderApi<P> targetSetter(Setter targetSetter) {
//		providedPropertyBuilder = providedPropertyBuilder.targetSetter(targetSetter);
		return this;
	}
	
	protected ProvidedPropertyBuilderApi<P> targetReference(boolean reference) {
//		providedPropertyBuilder = providedPropertyBuilder.targetReference(reference);
		return this;
	}

	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	public Projection<P> build(ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}	
	
//	protected Optional<ProjectionProperty> buildProperty(ProjectionRegistry factory) {
//		return providedPropertyBuilder.build(factory);
//	}

	public Optional<PropertyReader> buildPropertyReader(ProjectionRegistry registry) {

		return Optional.empty();	//FIXME
	}

	public Optional<PropertyWriter> buildPropertyWriter(ProjectionRegistry registry) {
		// TODO Auto-generated method stub
		return Optional.empty();	//FIXME
	}
}
