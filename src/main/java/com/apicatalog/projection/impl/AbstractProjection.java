package com.apicatalog.projection.impl;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;

abstract class AbstractProjection<P> implements Projection<P> {
	
	final Logger logger = LoggerFactory.getLogger(AbstractProjection.class);

	final ProjectionComposer<P> composer;
	final ProjectionExtractor<P> extractor;
	
	protected AbstractProjection(final ProjectionComposer<P> composer, final ProjectionExtractor<P> extractor) {
		this.composer = composer;
		this.extractor = extractor;
	}	
	
	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws CompositionError
	 */
	@Override
	public final P compose(Object... objects) throws CompositionError {
		return composer != null 
					? composer.compose(ProjectionStack.create(), CompositionContext.of(objects))
					: null
					;
	}

	/**
	 * Extract exact source value for the given projection
	 *
	 */
	@Override
	public final <S> Optional<S> extract(P projection, Class<S> objectType) throws ExtractionError {
		if (extractor == null) {
			return Optional.empty();
		}
		
		return extract(projection, null, objectType);
	}
	
	@Override
	public final <S> Optional<S> extract(P projection, String qualifier, Class<S> objectType) throws ExtractionError {

		if (extractor == null) {
			return Optional.empty();
		}

		if (projection == null || objectType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = 
					ExtractionContext
							.newInstance()
							.accept(qualifier, objectType, null);
		
		extractor.extract(projection, context);

		return context.get(qualifier, objectType, null).map(objectType::cast);
	}

	@Override
	public final <I> Optional<Collection<I>> extractCollection(P projection, Class<I> componentType) throws ExtractionError {
		
		if (extractor == null) {
			return Optional.empty();
		}

		return extractCollection(projection, null, componentType); 
	}
	
	@Override
	public final <I> Optional<Collection<I>> extractCollection(P projection, String qualifier, Class<I> componentType) throws ExtractionError {
		
		if (extractor == null) {
			return Optional.empty();
		}

		if (projection == null || componentType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = ExtractionContext.newInstance()
											.accept(qualifier, Collection.class, componentType);
		
		extractor.extract(projection, context);
	
		return context.get(qualifier, Collection.class, componentType).map(Collection.class::cast);
	}

	@Override
	public final Optional<ProjectionComposer<P>> getComposer() {
		return Optional.ofNullable(composer);
	}

	@Override
	public final Optional<ProjectionExtractor<P>> getExtractor() {
		return Optional.ofNullable(extractor);
	}
}
