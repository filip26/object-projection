package com.apicatalog.projection.api.object.impl;

import java.util.Optional;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.writer.ConstantWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ConstantPropertyApi<P> extends AbstractValueProviderApi<P> {
	
	final ProjectionApiImpl<P> projectionBuilder;
	
	final String[] constants; 
	
	Setter targetSetter;
	
	String targetProjectionName;

	protected ConstantPropertyApi(final ProjectionApiImpl<P> projectionBuilder, final String[] constants) {
		this.projectionBuilder = projectionBuilder;
		this.constants = constants;
	}
	
	@Override
	public AbstractValueProviderApi<P> targetGetter(final Getter targetGetter) {
		return this;
	}

	@Override
	public AbstractValueProviderApi<P> targetProjection(final String targetProjectionName) {
		this.targetProjectionName = targetProjectionName;
		return this;
	}

	@Override
	public Optional<PropertyReader> buildyReader(Registry registry) {
		return Optional.empty();
	}

	@Override
	protected AbstractValueProviderApi<P> targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
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
