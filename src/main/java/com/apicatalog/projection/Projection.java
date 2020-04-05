package com.apicatalog.projection;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.context.ProjectionContext;
import com.apicatalog.projection.objects.NamedObject;
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
	 * Decompose a projection into a source of values
	 * 
	 * @param projection to decompose
	 * @return objects extracted from the projection
	 * @throws ProjectionError
	 */
	public Object[] decompose(P projection) throws ProjectionError {
		return decompose(projection, ProjectionContext.of());
	}

	public final Object[] decompose(P projection, ProjectionContext context) throws ProjectionError {
		
		logger.debug("Decompose {}", projection.getClass().getSimpleName());
		
		final ProjectionQueue queue = ProjectionQueue.create().push(projection);
		
		for (int i = 0; i < properties.length; i++) {
			properties[i].backward(queue, context);
		}

		final Object ref = queue.pop();
		
		if (!ref.equals(projection)) {
			throw new IllegalStateException();
		}

		return context.getValues();
	}

	/**
	 * Extract exact source value for the given projection
	 * 
	 * @param sourceClass
	 * @param qualifier
	 * @param projection
	 * @return
	 * @throws ProjectionError 
	 */
	@SuppressWarnings("unchecked")
	public <S> S extract(Class<S> sourceObjectClass, String qualifier, P projection) throws ProjectionError {
		
		if (projection == null || sourceObjectClass == null) {
			throw new IllegalArgumentException();
		}
		
		logger.debug("Extract {} from {}", sourceObjectClass.getSimpleName(), projection.getClass().getSimpleName());

		//TODO optimize, don't use decompose
		Object[] objects = decompose(projection);
		
		if (objects == null || objects.length == 0) {
			return null;
		}

		if (StringUtils.isNotBlank(qualifier)) {
			return (S)Arrays
					.stream(objects)
					.filter(NamedObject.class::isInstance)
					.filter(o -> 
								qualifier.equals(((NamedObject<?>)o).getName())  
								&& sourceObjectClass.isInstance(((NamedObject<?>)o).getObject())
								)
					.findFirst()
					.orElse(null);
		}
		
		return (S)Arrays
					.stream(objects)
					.map(o -> NamedObject.class.isInstance(o) ? ((NamedObject<?>)o).getObject() : o)
					.filter(sourceObjectClass::isInstance)
					.findFirst()
					.orElse(null);
	}
	
	public ProjectionProperty[] getProperties() {
		return properties;
	}
	
	public final Class<P> getProjectionClass() {
		return projectionClass;
	}
}
