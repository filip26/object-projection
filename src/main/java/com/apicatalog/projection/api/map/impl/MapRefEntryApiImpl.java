package com.apicatalog.projection.api.map.impl;

import java.util.Optional;

import com.apicatalog.projection.Projection;
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

public final class MapRefEntryApiImpl extends AbstractMapEntryApi implements MapEntryApi, MapEntryBuildApi {
	
	final String projectionName;
	
	final Class<?> collectionType;
	
	protected MapRefEntryApiImpl(final MapProjectionBuilderApi projectionBuilder, final String name, final String projectionName) {
		this(projectionBuilder, name, null, projectionName);
	}
	
	protected MapRefEntryApiImpl(final MapProjectionBuilderApi projectionBuilder, final String name, final Class<?> collectionType, final String projectionName) {
		super(projectionBuilder, name);
		this.collectionType = collectionType;
		this.projectionName = projectionName;
	}

	@Override
	public Optional<PropertyReader> buildReader(final Registry registry) throws ProjectionError {

		if (valueProvider == null) {
			return Optional.empty();
		}
		
		final Projection<?> ref = registry.get(projectionName);
		
		if (ref == null) {
			throw new ProjectionError("Projection " + projectionName + " is not present.");
		}
		
		final ObjectType targetType = 
				collectionType != null 
					? ObjectType.of(collectionType, ref.getType())
					: ObjectType.of(ref.getType())
					;

		// extract getter
		final Getter targetGetter = MapEntryGetter.from(name, targetType);

		return valueProvider
					.targetGetter(targetGetter)
					.targetProjection(projectionName)
					.buildyReader(registry)
					;
	}
	
	@Override
	public Optional<PropertyWriter> buildWriter(final Registry registry) throws ProjectionError {

		if (valueProvider == null) {
			return Optional.empty();
		}

		final Projection<?> ref = registry.get(projectionName);
		
		if (ref == null) {
			throw new ProjectionError("Projection " + projectionName + " is not present.");
		}
		
		final ObjectType targetType = 
				collectionType != null 
					? ObjectType.of(collectionType, ref.getType())
					: ObjectType.of(ref.getType())
					;

		// extract setter
		final Setter targetSetter = MapEntrySetter.from(name, targetType);

		return valueProvider
					.targetSetter(targetSetter)
					.targetProjection(projectionName)
					.buildyWriter(registry)
					;		
	}
}