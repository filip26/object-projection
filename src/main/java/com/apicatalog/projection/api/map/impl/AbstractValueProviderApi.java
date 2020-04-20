package com.apicatalog.projection.api.map.impl;

import java.util.Optional;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public abstract class AbstractValueProviderApi extends MapProjectionApiWrapper {

	protected AbstractValueProviderApi(MapProjectionApi projectionBuilder) {
		super(projectionBuilder);
	}

	protected abstract AbstractValueProviderApi targetGetter(Getter targetGetter);
	
	protected abstract AbstractValueProviderApi targetSetter(Setter targetSetter);

	protected abstract AbstractValueProviderApi targetReference(boolean targetReference);

	protected abstract Optional<PropertyReader> buildyReader(ProjectionRegistry registry) throws ProjectionBuilderError;
	
	protected abstract Optional<PropertyWriter> buildyWriter(ProjectionRegistry registry) throws ProjectionBuilderError;

}