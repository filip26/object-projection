package com.apicatalog.projection.api.map.impl;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public final class MapEntryApiImpl extends MapProjectionApiWrapper implements MapEntryApi {
	
	final Logger logger = LoggerFactory.getLogger(MapEntryApiImpl.class);

	final String name;
	
	final Class<?> type;
	
	final Class<?> componentType;
	
	AbstractValueProviderApi valueProvider;

	protected MapEntryApiImpl(final MapProjectionApi projectionBuilder, final String name, final Class<?> type) {
		this(projectionBuilder, name, type, null);
	}
	
	protected MapEntryApiImpl(final MapProjectionApi projectionBuilder, final String name, final Class<?> collectionType, final Class<?> componentType) {
		super(projectionBuilder);
		this.name = name;
		this.type = collectionType;
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
		
		final MapArraySourceApiImpl sourcesPropertyApi = 
				new MapArraySourceApiImpl(projectionBuilder, name);
//TODO						.targetReference(targetReference);
		
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
				new MapProvidedApiImpl(projectionBuilder, StringUtils.isNotBlank(qualifier) ? qualifier : null)
//						.targetReference(targetReference);
					;
		
		this.valueProvider = providedPropertyApi;
		
		return providedPropertyApi;
	}

	@Override
	public MapProjectionApi constant(final String...values) {

		if (logger.isTraceEnabled()) {
			logger.trace("constant({})", Arrays.toString(values));
		}
		
		this.valueProvider = 
				new MapConstantPropertyApi(projectionBuilder, values)
						.targetReference(false);
		
		return projectionBuilder;
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