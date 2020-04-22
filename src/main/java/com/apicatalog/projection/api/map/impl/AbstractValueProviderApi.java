package com.apicatalog.projection.api.map.impl;

import java.util.Optional;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

abstract class AbstractValueProviderApi extends MapProjectionApiWrapper {

	protected AbstractValueProviderApi(MapProjectionBuilderApi projectionBuilder) {
		super(projectionBuilder);
	}

	protected abstract AbstractValueProviderApi targetGetter(Getter targetGetter);
	
	protected abstract AbstractValueProviderApi targetSetter(Setter targetSetter);

	protected abstract AbstractValueProviderApi targetReference(boolean targetReference);

	protected abstract Optional<PropertyReader> buildyReader(ProjectionRegistry registry) throws ProjectionError;
	
	protected abstract Optional<PropertyWriter> buildyWriter(ProjectionRegistry registry) throws ProjectionError;

}