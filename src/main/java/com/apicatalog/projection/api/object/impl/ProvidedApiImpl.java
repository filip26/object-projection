package com.apicatalog.projection.api.object.impl;

import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.object.ObjectPropertyApi;
import com.apicatalog.projection.api.object.ObjectProvidedApi;
import com.apicatalog.projection.builder.reader.ProvidedPropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.ProvidedPropertyWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ProvidedApiImpl<P> extends AbstractValueProviderApi<P> implements ObjectProvidedApi<P> {
	
	final ProjectionApiImpl<P> projectionBuilder;

	final String qualifier;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	boolean optional;
	
	protected ProvidedApiImpl(final ProjectionApiImpl<P> projection, final String name) {
		this.projectionBuilder = projection;
		this.qualifier = name;
	}
	
	@Override
	public ObjectProvidedApi<P> optional() {
		this.optional = true;
		return this;
	}

	@Override
	public ObjectProvidedApi<P> required() {
		this.optional = false;
		return this;
	}
	
	@Override
	public ObjectPropertyApi<P> map(final String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	@Override
	public ObjectPropertyApi<P> map(final String propertyName, final boolean reference) {
		return projectionBuilder.map(propertyName, reference);
	}

	public Projection<P> build(final ProjectionRegistry registry) throws ProjectionBuilderError {
		return projectionBuilder.build(registry);
	}	
	
	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionBuilderError {
		return ProvidedPropertyReaderBuilder
						.newInstance()
							.qualifier(qualifier)
							.optional(optional)
							.targetGetter(targetGetter)
							.targetReference(targetReference)
							.build(registry)
								.map(PropertyReader.class::cast);
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(final ProjectionRegistry registry) throws ProjectionBuilderError {
		return ProvidedPropertyWriterBuilder
						.newInstance()
							.qualifier(qualifier)
							.optional(optional)
							.targetSetter(targetSetter)
							.targetReference(targetReference)
							.build(registry)
								.map(PropertyWriter.class::cast);
	}
	
	@Override
	protected ProvidedApiImpl<P> targetGetter(final Getter targetGetter) {
		this.targetGetter = targetGetter;
		return this;
	}

	@Override
	protected ProvidedApiImpl<P> targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	@Override
	protected ProvidedApiImpl<P> targetReference(final boolean reference) {
		this.targetReference = reference;
		return this;
	}
}
