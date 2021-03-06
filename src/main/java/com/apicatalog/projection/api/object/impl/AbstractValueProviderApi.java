package com.apicatalog.projection.api.object.impl;

import java.util.Optional;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

abstract class AbstractValueProviderApi<P> {

	protected abstract AbstractValueProviderApi<P> targetGetter(Getter targetGetter);
	
	protected abstract AbstractValueProviderApi<P> targetSetter(Setter targetSetter);

	protected abstract AbstractValueProviderApi<P> targetProjection(String targetProjectionName);

	protected abstract Optional<PropertyReader> buildyReader(Registry registry) throws ProjectionError;
	
	protected abstract Optional<PropertyWriter> buildyWriter(Registry registry) throws ProjectionError;	
}