package com.apicatalog.projection.api.map.impl;

import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.builder.writer.ConstantWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public class MapConstantPropertyApi extends AbstractValueProviderApi<Map<String, Object>> {
	
	final MapProjectionApi projectionBuilder;
	
	final String[] constants; 
	
	Setter targetSetter;
	
	boolean targetReference;

	protected MapConstantPropertyApi(final MapProjectionApi projectionBuilder, String[] constants) {
		this.projectionBuilder = projectionBuilder;
		this.constants = constants;
	}
	
	@Override
	public AbstractValueProviderApi<Map<String, Object>> targetGetter(Getter targetGetter) {
		return this;
	}

	@Override
	public AbstractValueProviderApi<Map<String, Object>> targetReference(boolean targetReference) {
		this.targetReference = targetReference;
		return this;
	}

	@Override
	public Optional<PropertyReader> buildyReader(ProjectionRegistry registry) {
		return Optional.empty();
	}

	@Override
	protected AbstractValueProviderApi<Map<String, Object>> targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(final ProjectionRegistry registry) throws ProjectionBuilderError {
		return ConstantWriterBuilder.newInstance()
					.constants(constants)
					.targetSetter(targetSetter, targetReference)
					.build(registry)
					.map(PropertyWriter.class::cast)
					;
	}

	
}
