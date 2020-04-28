package com.apicatalog.projection.api.map.impl;

import java.util.Optional;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.map.MapEntryApi;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.getter.MapEntryGetter;
import com.apicatalog.projection.object.setter.MapEntrySetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapEntryApiImpl extends AbstractMapEntryApi implements MapEntryApi, MapEntryBuildApi {
	
	final Class<?> type;
	
	final Class<?> componentType;
	
	protected MapEntryApiImpl(final MapProjectionBuilderApi projectionBuilder, final String name, final Class<?> type) {
		this(projectionBuilder, name, type, null);
	}
	
	protected MapEntryApiImpl(final MapProjectionBuilderApi projectionBuilder, final String name, final Class<?> collectionType, final Class<?> componentType) {
		super(projectionBuilder, name);
		this.type = collectionType;
		this.componentType = componentType;
	}
		
	@Override
	public Optional<PropertyReader> buildReader(final Registry registry) throws ProjectionError {

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
	
	@Override
	public Optional<PropertyWriter> buildWriter(final Registry registry) throws ProjectionError {

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