package com.apicatalog.projection.api.map.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.api.map.MapArraySourceApi;
import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.api.map.MapProvidedApi;
import com.apicatalog.projection.api.map.MapSingleSourceApi;

abstract class AbstractMapEntryApi extends MapProjectionApiWrapper implements MapEntryApi, MapEntryBuildApi {
	
	final Logger logger =  LoggerFactory.getLogger(getClass());

	final String name;
		
	AbstractValueProviderApi valueProvider;

	protected AbstractMapEntryApi(final MapProjectionBuilderApi projectionBuilder, final String name) {
		super(projectionBuilder);
		this.name = name;
	}

	@Override
	public MapSingleSourceApi source(Class<?> sourceClass) {
		return source(sourceClass, name);
	}

	@Override
	public MapSingleSourceApi source(Class<?> sourceClass, String sourceProperty) {

		if (logger.isTraceEnabled()) {
			logger.trace("source({}, {})", sourceClass.getSimpleName(), sourceProperty);
		}
		
		final MapSingleSourceApiImpl sourcePropertyApi = 
				new MapSingleSourceApiImpl(
					projectionBuilder,
					sourceClass,
					// use the same name if source property name is not present
					StringUtils.isNotBlank(sourceProperty) ? sourceProperty : name
					);

		this.valueProvider = sourcePropertyApi;
		
		return sourcePropertyApi;
	}

	@Override
	public MapArraySourceApi sources() {
		
		if (logger.isTraceEnabled()) {
			logger.trace("sources()");
		}
		
		final MapArraySourceApiImpl sourcesPropertyApi = 
				new MapArraySourceApiImpl(projectionBuilder, name);
		
		this.valueProvider = sourcesPropertyApi;
		
		return sourcesPropertyApi;
	}

	@Override
	public MapProvidedApi provided() {
		return provided(null);
	}
	
	@Override
	public MapProvidedApi provided(final String qualifier) {
		
		if (logger.isTraceEnabled()) {
			logger.trace("provided({})", qualifier);
		}
		
		final MapProvidedApiImpl providedPropertyApi = 
				new MapProvidedApiImpl(projectionBuilder, StringUtils.isNotBlank(qualifier) ? qualifier : null);
		
		this.valueProvider = providedPropertyApi;
		
		return providedPropertyApi;
	}

	@Override
	public MapProjectionBuilderApi constant(final String...values) {

		if (logger.isTraceEnabled()) {
			logger.trace("constant({})", Arrays.toString(values));
		}
		
		this.valueProvider = 
				new MapConstantPropertyApi(projectionBuilder, values);
		
		return projectionBuilder;
	}		
}