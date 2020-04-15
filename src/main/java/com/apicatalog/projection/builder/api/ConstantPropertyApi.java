package com.apicatalog.projection.builder.api;

import java.util.Optional;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.writer.ConstantWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public class ConstantPropertyApi<P> extends AbstractValueProviderApi<P> {
	
	final ProjectionBuilder<P> projectionBuilder;
	
	final String[] constants; 
	
	Setter targetSetter;
	
	boolean targetReference;

	protected ConstantPropertyApi(final ProjectionBuilder<P> projectionBuilder, String[] constants) {
		this.projectionBuilder = projectionBuilder;
		this.constants = constants;
	}
	
	@Override
	public AbstractValueProviderApi<P> targetGetter(Getter targetGetter) {
		return this;
	}

	@Override
	public AbstractValueProviderApi<P> targetReference(boolean targetReference) {
		this.targetReference = targetReference;
		return this;
	}

	@Override
	public Optional<PropertyReader> buildyReader(ProjectionRegistry registry) {
		return Optional.empty();
	}

	@Override
	protected AbstractValueProviderApi<P> targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(final ProjectionRegistry registry) throws ProjectionError {
		return ConstantWriterBuilder.newInstance()
					.constants(constants)
					.targetSetter(targetSetter, targetReference)
					.build(registry)
					.map(PropertyWriter.class::cast)
					;
	}

	
}