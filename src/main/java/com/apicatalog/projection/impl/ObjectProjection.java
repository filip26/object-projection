package com.apicatalog.projection.impl;

import java.util.Collection;

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

public final class ObjectProjection<P> implements Projection<P> {
	
	final Logger logger = LoggerFactory.getLogger(ObjectProjection.class);

	final Class<P> projectionClass;

	final ObjectProjectionComposer<P> composer;
	final ObjectProjectionExtractor<P> extractor;
	
	protected ObjectProjection(final Class<P> projectionClass, final ObjectProjectionComposer<P> composer, final ObjectProjectionExtractor<P> extractor) {
		this.projectionClass = projectionClass;
		this.composer = composer;
		this.extractor = extractor;
	}
	
	public static final <A> ObjectProjection<A> newInstance(final Class<A> projectionClass, final PropertyReader[] readers, final PropertyWriter[] writers) {
		return new ObjectProjection<>(
						projectionClass, 
						ObjectProjectionComposer.newInstance(projectionClass, writers),
						ObjectProjectionExtractor.newInstance(readers)
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
	public P compose(Object... objects) throws ProjectionError {
		return composer.compose(ProjectionStack.create(), CompositionContext.of(objects));
	}

	/**
	 * Extract exact source value for the given projection
	 *
	 */
	@Override
	public <S> S extract(P projection, Class<S> objectType) throws ProjectionError {
		return extract(projection, null, objectType);
	}
	
	@Override
	public <S> S extract(P projection, String qualifier, Class<S> objectType) throws ProjectionError {

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
	public <I> Collection<I> extractCollection(P projection, Class<I> componentType) throws ProjectionError {
		return extractCollection(projection, null, componentType); 
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <I> Collection<I> extractCollection(P projection, String qualifier, Class<I> componentType) throws ProjectionError {
		
		if (projection == null || componentType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = ExtractionContext.newInstance()
											.accept(qualifier, Collection.class, componentType);
		
		extractor.extract(projection, context);
	
		return (Collection<I>) context.get(qualifier, Collection.class, componentType).orElse(null);
	}

	public final String getName() {
		return projectionClass.getCanonicalName();
	}

	@Override
	public Class<?> getType() {
		return projectionClass;
	}

	@Override
	public ProjectionComposer<P> getComposer() {
		return composer;
	}

	@Override
	public ProjectionExtractor<P> getExtractor() {
		return extractor;
	}
}
