package com.apicatalog.projection;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.property.ProjectionProperty;

public final class Projection<P> {
	
	final Logger logger = LoggerFactory.getLogger(Projection.class);

	final Class<P> projectionClass;
	
	final ProjectionProperty[] properties;
	
	Projection(final Class<P> projectionClass, final ProjectionProperty[] properties) {
		this.projectionClass = projectionClass;
		this.properties = properties;
	}
	
	public static final <A> Projection<A> newInstance(final Class<A> projectionClass, final ProjectionProperty[] properties) {
		return new Projection<>(projectionClass, properties);
	}
	
	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	public P compose(Object... objects) throws ProjectionError {
		return compose(ProjectionQueue.create(), CompositionContext.of(objects));
	}

	public P compose(ProjectionQueue queue, CompositionContext context) throws ProjectionError {
		
		logger.debug("Compose {} of {} object(s), depth = {}", projectionClass.getSimpleName(), context.size(), queue.length());

		if (logger.isTraceEnabled()) {
			context.stream().forEach(v -> logger.trace("  {}", v));
		}

		// check for cycles
		if (queue.contains(projectionClass)) {
			logger.debug("Ignored. Projection {} is in processing already", projectionClass.getSimpleName());
			return null;
		}
		
		final P projection = ObjectUtils.newInstance(projectionClass);
		
		queue.push(projection);

		for (int i = 0; i < properties.length; i++) {
			
			// limit property visibility
			if (properties[i].isVisible(queue.length() - 1)) {
				properties[i].forward(queue, context);				
			}

		}
		
		final Object ref = queue.pop();
		
		if (!ref.equals(projection)) {
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
	
	@SuppressWarnings("unchecked")
	public <S> S extract(P projection, String qualifier, Class<S> objectType) throws ProjectionError {

		final ExtractionContext context = ExtractionContext.newInstance()
												.accept(qualifier, objectType, null);
		
		extract(projection, context);

		return (S) context.get(qualifier, objectType, null);
	}

	public <I> Collection<I> extractCollection(P projection, Class<I> componentType) throws ProjectionError {
		return extractCollection(projection, null, componentType); 
	}
	
	@SuppressWarnings("unchecked")
	public <I> Collection<I> extractCollection(P projection, String qualifier, Class<I> componentType) throws ProjectionError {
		
		final ExtractionContext context = ExtractionContext.newInstance()
											.accept(qualifier, Collection.class, componentType);
		
		extract(projection, context);
	
		return (Collection<I>) context.get(qualifier, Collection.class, componentType);
	}

	public void extract(P projection, ExtractionContext context) throws ProjectionError {
		
		if (projection == null || context == null) {
			throw new IllegalArgumentException();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Extract {} object(s) from {}, {} properties", context.size(), projection.getClass().getSimpleName(),  Optional.ofNullable(properties).orElse(new ProjectionProperty[0]).length);
		}

		final ProjectionQueue queue = ProjectionQueue.create().push(projection);
		
		for (int i = 0; i < properties.length; i++) {
			properties[i].backward(queue, context);
		}

		final Object ref = queue.pop();
		
		if (!ref.equals(projection)) {
			throw new IllegalStateException();
		}
	}
	
	public ProjectionProperty[] getProperties() {
		return properties;
	}
	
	public final Class<P> getProjectionClass() {
		return projectionClass;
	}
}
