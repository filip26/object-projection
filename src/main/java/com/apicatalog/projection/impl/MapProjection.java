package com.apicatalog.projection.impl;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjection implements Projection<Map<String, Object>> {
	
	final Logger logger = LoggerFactory.getLogger(MapProjection.class);

	final MapProjectionComposer composer;
	final MapProjectionExtractor extractor;
	
	final String name;
	
	protected MapProjection(final String name, final MapProjectionComposer composer, final MapProjectionExtractor extractor) {
		this.name = name;
		this.composer = composer;
		this.extractor = extractor;
	}
	
	public static final Projection<Map<String, Object>> newInstance(final String name, final PropertyReader[] readers, final PropertyWriter[] writers) {
		return new MapProjection(
						name, 
						MapProjectionComposer.newInstance(writers),
						MapProjectionExtractor.newInstance(readers)
						);
	}

	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	@Override
	public Map<String, Object> compose(Object... objects) throws ProjectionError {
		return composer.compose(ProjectionStack.create(), CompositionContext.of(objects));
	}

	/**
	 * Extract exact source value for the given projection
	 *
	 */
	@Override
	public <S> S extract(Map<String, Object> projection, Class<S> objectType) throws ProjectionError {
		return extract(projection, null, objectType);
	}
	
	@Override
	public <S> S extract(Map<String, Object> projection, String qualifier, Class<S> objectType) throws ProjectionError {

		if (projection == null || objectType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = 
					ExtractionContext
							.newInstance()
							.accept(qualifier, objectType, null);
		
		extractor.extract(projection, context);

		return context.get(qualifier, objectType, null).map(objectType::cast).orElse(null);
	}

	@Override
	public <I> Collection<I> extractCollection(Map<String, Object> projection, Class<I> componentType) throws ProjectionError {
		return extractCollection(projection, null, componentType); 
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <I> Collection<I> extractCollection(Map<String, Object> projection, String qualifier, Class<I> componentType) throws ProjectionError {
		
		if (projection == null || componentType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = ExtractionContext.newInstance()
											.accept(qualifier, Collection.class, componentType);
		
		extractor.extract(projection, context);
	
		return (Collection<I>) context.get(qualifier, Collection.class, componentType).orElse(null);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getType() {
		return Map.class;
	}

	@Override
	public ProjectionComposer<Map<String, Object>> getComposer() {
		return composer;
	}

	@Override
	public ProjectionExtractor<Map<String, Object>> getExtractor() {
		return extractor;
	}
}
