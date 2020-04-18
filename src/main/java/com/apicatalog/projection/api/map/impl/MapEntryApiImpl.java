package com.apicatalog.projection.api.map.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.map.MapArraySourceApi;
import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionApi;
import com.apicatalog.projection.api.map.MapProvidedApi;
import com.apicatalog.projection.api.map.MapSingleSourceApi;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.getter.MapEntryGetter;
import com.apicatalog.projection.object.setter.MapEntrySetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapEntryApiImpl implements MapEntryApi {
	
	final Logger logger = LoggerFactory.getLogger(MapEntryApiImpl.class);

	final MapProjectionApi projectionBuilder;

	final String name;
	
	final Class<?> type;
	
	final Class<?> componentType;
	
	AbstractValueProviderApi<Map<String, Object>> valueProvider;
	
	protected MapEntryApiImpl(MapProjectionApi projectionBuilder, String name, Class<?> type, Class<?> componentType) {
		this.projectionBuilder = projectionBuilder;
		this.name = name;
		this.type = type;
		this.componentType = componentType;
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
					)
				.targetReference(false);
		
		this.valueProvider = sourcePropertyApi;
		
		return sourcePropertyApi;
	}

	@Override
	public MapArraySourceApi sources() {
		
		if (logger.isTraceEnabled()) {
			logger.trace("sources()");
		}
		
//		final ArraySourceApiImpl<P> sourcesPropertyApi = 
//				new ArraySourceApiImpl<>(projectionBuilder, targetPropertyName)
//						.targetReference(targetReference);
//		
//		this.valueProvider = sourcesPropertyApi;
//		
//		return sourcesPropertyApi;
		return null;
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
		
//		final ProvidedApiImpl<P> providedPropertyApi = 
//				new ProvidedApiImpl<>(projectionBuilder, StringUtils.isNotBlank(qualifier) ? qualifier : null)
//						.targetReference(targetReference);
//		
//		this.valueProvider = providedPropertyApi;
//		
//		return providedPropertyApi;
		return null;
	}

	@Override
	public MapProjectionApi constant(final String...values) {

		if (logger.isTraceEnabled()) {
			logger.trace("constant({})", Arrays.toString(values));
		}
		
		this.valueProvider = 
				new MapConstantPropertyApi(projectionBuilder, values)
						.targetReference(false);	//targetReference);
		
		return projectionBuilder;
	}
	
	@Override
	public MapEntryApi map(String name, Class<?> type) {
		return projectionBuilder.map(name, type, null);
	}

	@Override
	public MapEntryApi map(String name, Class<?> type, Class<?> componentType) {
		return projectionBuilder.map(name, type, componentType);
	}
	
	@Override
	public Projection<Map<String, Object>> build(final ProjectionRegistry registry) throws ProjectionBuilderError {
		return projectionBuilder.build(registry);
	}
	
	protected Optional<PropertyReader> buildReader(final ProjectionRegistry registry) throws ProjectionBuilderError {

		if (valueProvider == null) {
			return Optional.empty();
		}
		
		final ObjectType targetType = ObjectType.of(type, componentType);

		// extract getter
		final Getter targetGetter = MapEntryGetter.from(name, targetType);

		return valueProvider
					.targetGetter(targetGetter)
					.buildyReader(registry)
					;
	}
	
	protected Optional<PropertyWriter> buildWriter(final ProjectionRegistry registry) throws ProjectionBuilderError {

		if (valueProvider == null) {
			return Optional.empty();
		}

		final ObjectType targetType = ObjectType.of(type, componentType);

		// extract setter
		final Setter targetSetter = MapEntrySetter.from(name, targetType);

		return valueProvider
					.targetSetter(targetSetter)
					.buildyWriter(registry)
					;		
	}
}