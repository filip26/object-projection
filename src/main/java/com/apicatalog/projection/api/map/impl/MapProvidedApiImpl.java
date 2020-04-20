package com.apicatalog.projection.api.map.impl;

import java.util.Optional;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapProjectionApi;
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
	
	protected MapProvidedApiImpl(final MapProjectionApi projection, final String name) {
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
}
