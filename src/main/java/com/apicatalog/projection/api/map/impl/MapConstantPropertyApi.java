package com.apicatalog.projection.api.map.impl;

import java.util.Optional;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.builder.writer.ConstantWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapConstantPropertyApi extends AbstractValueProviderApi {
	
	final String[] constants; 
	
	Setter targetSetter;
	
	String targetProjectionName;

	protected MapConstantPropertyApi(final MapProjectionBuilderApi projectionBuilder, String[] constants) {
		super(projectionBuilder);
		this.constants = constants;
	}
	
	@Override
	protected AbstractValueProviderApi targetGetter(final Getter targetGetter) {
		return this;
	}

	@Override
	protected AbstractValueProviderApi targetProjection(final String targetProjectionName) {
		this.targetProjectionName = targetProjectionName;
		return this;
	}

	@Override
	protected AbstractValueProviderApi targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}

	@Override
	protected Optional<PropertyReader> buildyReader(Registry registry) {
		return Optional.empty();
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(final Registry registry) throws ProjectionError {
		return ConstantWriterBuilder.newInstance()
					.constants(constants)
					.targetSetter(targetSetter)
					.targetProjection(targetProjectionName)
					.build(registry)
					.map(PropertyWriter.class::cast)
					;
	}
}
