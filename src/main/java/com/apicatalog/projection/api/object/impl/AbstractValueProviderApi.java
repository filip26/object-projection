package com.apicatalog.projection.api.object.impl;

import java.util.Optional;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public abstract class AbstractValueProviderApi<P> {

	protected abstract AbstractValueProviderApi<P> targetGetter(Getter targetGetter);
	
	protected abstract AbstractValueProviderApi<P> targetSetter(Setter targetSetter);

	protected abstract AbstractValueProviderApi<P> targetReference(boolean targetReference);

	protected abstract Optional<PropertyReader> buildyReader(ProjectionRegistry registry) throws ProjectionBuilderError;
	
	protected abstract Optional<PropertyWriter> buildyWriter(ProjectionRegistry registry) throws ProjectionBuilderError;	
}