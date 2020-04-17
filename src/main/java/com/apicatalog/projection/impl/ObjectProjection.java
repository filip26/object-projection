package com.apicatalog.projection.impl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ObjectProjection<P> implements Projection<P> {
	
	final Logger logger = LoggerFactory.getLogger(ObjectProjection.class);

	final Class<P> projectionClass;
	
	final PropertyReader[] readers;
	final PropertyWriter[] writers;
	
	protected ObjectProjection(final Class<P> projectionClass, final PropertyReader[] readers, final PropertyWriter[] writers) {
		this.projectionClass = projectionClass;
		this.readers = readers;
		this.writers = writers;
	}
	
	public static final <A> ObjectProjection<A> newInstance(final Class<A> projectionClass, final PropertyReader[] readers, final PropertyWriter[] writers) {
		return new ObjectProjection<>(projectionClass, readers, writers);
	}

	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	public P compose(Object... objects) throws ProjectionError {
		return compose(ProjectionStack.create(), CompositionContext.of(objects));
	}

	public P compose(ProjectionStack stack, CompositionContext context) throws ProjectionError {
		
		if (stack == null || context == null || writers == null) {
			throw new IllegalArgumentException();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Compose {} of {} object(s), {} properties, depth = {}", projectionClass.getSimpleName(), context.size(), writers.length, stack.length());
		
			if (logger.isTraceEnabled()) {
				context.stream().forEach(sourceObject -> logger.trace("  {}", sourceObject));
			}
		}

		// check for cycles
		if (stack.contains(projectionClass)) {
			logger.debug("Ignored. Projection {} is in processing already", projectionClass.getSimpleName());
			return null;
		}
		
		final P projection = ObjectUtils.newInstance(projectionClass);
		
		stack.push(projection);

		for (PropertyWriter writer : writers) {
			// limit property visibility
			if (writer.isVisible(stack.length() - 1)) {
				writer.write(stack, context);				
			}
		}

		if (!projection.equals(stack.pop())) {
			throw new IllegalStateException();
		}
		
		return projection;
	}

	/**
	 * Extract exact source value for the given projection
	 *
	 */
	public <S> S extract(P projection, Class<S> objectType) throws ProjectionError {
		return extract(projection, null, objectType);
	}
	
	public <S> S extract(P projection, String qualifier, Class<S> objectType) throws ProjectionError {

		if (projection == null || objectType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = 
					ExtractionContext
							.newInstance()
							.accept(qualifier, objectType, null);
		
		extract(projection, context);

		return context.get(qualifier, objectType, null).map(objectType::cast).orElse(null);
	}

	public <I> Collection<I> extractCollection(P projection, Class<I> componentType) throws ProjectionError {
		return extractCollection(projection, null, componentType); 
	}
	
	@SuppressWarnings("unchecked")
	public <I> Collection<I> extractCollection(P projection, String qualifier, Class<I> componentType) throws ProjectionError {
		
		if (projection == null || componentType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = ExtractionContext.newInstance()
											.accept(qualifier, Collection.class, componentType);
		
		extract(projection, context);
	
		return (Collection<I>) context.get(qualifier, Collection.class, componentType).orElse(null);
	}

	public void extract(P projection, ExtractionContext context) throws ProjectionError {
		
		if (projection == null || context == null || readers == null) {
			throw new IllegalArgumentException();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Extract {} object(s) from {}, {} properties", context.size(), projection.getClass().getSimpleName(), readers.length);
		}

		final ProjectionStack stack = ProjectionStack.create().push(projection);
		
		for (PropertyReader reader : readers) {
			reader.read(stack, context);			
		}
		
		if (!projection.equals(stack.pop())) {
			throw new IllegalStateException();
		}
	}
	
	public final Class<P> getProjectionClass() {
		return projectionClass;
	}
}
