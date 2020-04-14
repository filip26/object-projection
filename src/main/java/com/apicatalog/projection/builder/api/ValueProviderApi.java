package com.apicatalog.projection.builder.api;

import java.util.Optional;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

abstract class ValueProviderApi<P> {

	protected abstract ValueProviderApi<P> targetGetter(Getter targetGetter);
	
	protected abstract ValueProviderApi<P> targetSetter(Setter targetSetter);

	protected abstract ValueProviderApi<P> targetReference(boolean targetReference);

	protected abstract Optional<PropertyReader> buildyReader(ProjectionRegistry registry) throws ProjectionError;
	
	protected abstract Optional<PropertyWriter> buildyWriter(ProjectionRegistry registry) throws ProjectionError;	
}