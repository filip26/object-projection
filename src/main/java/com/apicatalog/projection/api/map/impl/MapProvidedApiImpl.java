package com.apicatalog.projection.api.map.impl;

import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.api.map.MapProvidedApi;
import com.apicatalog.projection.builder.reader.ProvidedPropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.ProvidedPropertyWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProvidedApiImpl extends AbstractValueProviderApi implements MapProvidedApi {
	
	final String qualifier;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	boolean optional;
	
	protected MapProvidedApiImpl(final MapProjectionBuilderApi projection, final String name) {
		super(projection);
		this.qualifier = name;
	}
	
	@Override
	public MapProvidedApi optional() {
		this.optional = true;
		return this;
	}

	@Override
	public MapProvidedApi required() {
		this.optional = false;
		return this;
	}
	
	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionError {
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
	protected Optional<PropertyWriter> buildyWriter(final ProjectionRegistry registry) throws ProjectionError {
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
	protected MapProvidedApiImpl targetGetter(final Getter targetGetter) {
		this.targetGetter = targetGetter;
		return this;
	}

	@Override
	protected MapProvidedApiImpl targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	@Override
	protected MapProvidedApiImpl targetReference(final boolean reference) {
		this.targetReference = reference;
		return this;
	}

	@Override
	public Projection<Map<String, Object>> build(ProjectionRegistry registry) throws ProjectionError {
		return projectionBuilder.build(registry);
	}
}
