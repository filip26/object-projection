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
		providedPropertyReaderBuilder.targetGetter(targetGetter);
		return this;
	}

	protected ProvidedPropertyBuilderApi<P> targetSetter(Setter targetSetter) {
		providedPropertyWriterBuilder.targetSetter(targetSetter);
		return this;
	}
	
	protected ProvidedPropertyBuilderApi<P> targetReference(boolean reference) {
		providedPropertyReaderBuilder.targetReference(reference);
		providedPropertyWriterBuilder.targetReference(reference);
		return this;
	}

	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	public Projection<P> build(ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}	
	
	public Optional<PropertyReader> buildPropertyReader(final ProjectionRegistry registry) throws ProjectionError {
		return providedPropertyReaderBuilder.build(registry).map(PropertyReader.class::cast);
	}

	public Optional<PropertyWriter> buildPropertyWriter(final ProjectionRegistry registry) throws ProjectionError {
		return providedPropertyWriterBuilder.build(registry).map(PropertyWriter.class::cast);
	}
}
