package com.apicatalog.projection.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class MapProjection implements Projection<Map<String, Object>> {
	
	final Logger logger = LoggerFactory.getLogger(MapProjection.class);
	
	final PropertyReader[] readers;
	final PropertyWriter[] writers;
	
	final String name;
	
	protected MapProjection(final String name, final PropertyReader[] readers, final PropertyWriter[] writers) {
		this.name = name;
		this.readers = readers;
		this.writers = writers;
	}
	
	public static final Projection<Map<String, Object>> newInstance(final String name, final PropertyReader[] readers, final PropertyWriter[] writers) {
		return new MapProjection(name, readers, writers);
	}

	/**
	 * Compose a projection from the given source values
	 * 
	 * @param objects used to compose a projection
	 * @return a projection
	 * @throws ProjectionError
	 */
	public Map<String, Object> compose(Object... objects) throws ProjectionError {
		return compose(ProjectionStack.create(), CompositionContext.of(objects));
	}

	public Map<String, Object> compose(ProjectionStack stack, CompositionContext context) throws ProjectionError {
		
		if (stack == null || context == null || writers == null) {
			throw new IllegalArgumentException();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Compose Map of {} object(s), {} properties, depth = {}", context.size(), writers.length, stack.length());
		
			if (logger.isTraceEnabled()) {
				context.stream().forEach(sourceObject -> logger.trace("  {}", sourceObject));
			}
		}

//TODO		// check for cycles
//		if (stack.contains(projectionClass)) {
//			logger.debug("Ignored. Projection {} is in processing already", projectionClass.getSimpleName());
//			return null;
//		}
		
		final Map<String, Object> projection = new HashMap<>(); 
		
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
	public <S> S extract(Map<String, Object> projection, Class<S> objectType) throws ProjectionError {
		return extract(projection, null, objectType);
	}
	
	public <S> S extract(Map<String, Object> projection, String qualifier, Class<S> objectType) throws ProjectionError {

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

	public <I> Collection<I> extractCollection(Map<String, Object> projection, Class<I> componentType) throws ProjectionError {
		return extractCollection(projection, null, componentType); 
	}
	
	@SuppressWarnings("unchecked")
	public <I> Collection<I> extractCollection(Map<String, Object> projection, String qualifier, Class<I> componentType) throws ProjectionError {
		
		if (projection == null || componentType == null) {
			throw new IllegalArgumentException();
		}

		final ExtractionContext context = ExtractionContext.newInstance()
											.accept(qualifier, Collection.class, componentType);
		
		extract(projection, context);
	
		return (Collection<I>) context.get(qualifier, Collection.class, componentType).orElse(null);
	}

	public void extract(Map<String, Object> projection, ExtractionContext context) throws ProjectionError {
		
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

	@Override
	public String getName() {
		return name;
	}

}
