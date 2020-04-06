package com.apicatalog.projection;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.context.ProjectionContext;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.source.SourceObject;

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
		return compose(ProjectionQueue.create(), ProjectionContext.of(objects));
	}

	public P compose(ProjectionQueue queue, ProjectionContext context) throws ProjectionError {
		
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
	
	public <S> S extract(P projection, String qualifier, Class<S> objectType) throws ProjectionError {

		S object = ObjectUtils.newInstance(objectType);
		
		extract(projection, ProjectionContext.of(StringUtils.isNotBlank(qualifier) ? SourceObject.of(qualifier, object) : object));
		
		return object;
	}

	public <I> Collection<I> extractCollection(P projection, Class<I> componentType) throws ProjectionError {
		return extractCollection(projection, null, componentType); 
	}
	
	public <I> Collection<I> extractCollection(P projection, String qualifier, Class<I> componentType) throws ProjectionError {
		
		final ProjectionContext context = ProjectionContext.of();
//		for (Class<?> clazz : types) {
//			context.addOrReplace(ObjectUtils.newInstance(clazz));
//		}
//		
//		extract(projection, context);
//
//		return context.getValues();
		return null;
	}

	public void extract(P projection, ProjectionContext context) throws ProjectionError {
		
		logger.debug("Extract {} object(s) from {}", context.size(), projection.getClass().getSimpleName());

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
