package com.apicatalog.projection.api.map.impl;

import java.util.Optional;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

interface MapEntryBuildApi {

	Optional<PropertyReader> buildReader(final Registry registry) throws ProjectionError;
	
	Optional<PropertyWriter> buildWriter(final Registry registry) throws ProjectionError;
}
