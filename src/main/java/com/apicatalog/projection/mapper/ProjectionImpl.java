package com.apicatalog.projection.mapper;

import java.util.Arrays;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.property.ProjectionProperty;

class ProjectionImpl<P> implements Projection<P> {
	
	final Logger logger = LoggerFactory.getLogger(ProjectionImpl.class);

	final Class<P> projectionClass;
	
	ProjectionProperty[] properties;
	
	public ProjectionImpl(Class<P> projectionClass) {
		this.projectionClass = projectionClass;
	}
	
	@Override
	public Class<P> getProjectionClass() {
		return projectionClass;
	}

	@Override
	public P compose(Object... objects) throws ProjectionError {
		return compose(ProjectionQueue.create(), objects);
	}
	
	@Override
	public P compose(ProjectionQueue queue, Object... objects) throws ProjectionError {
		
		logger.debug("Compose {} of {} object(s), depth = {}", projectionClass.getSimpleName(), objects.length, queue.length());
		
		if (logger.isTraceEnabled()) {
			Stream.of(objects).forEach(v -> logger.trace("  {}", v.getClass().getSimpleName()));
		}
		
		// check for cycles
		if (queue.contains(projectionClass)) {
			logger.debug("Ignored. Projection {} is in processing already", projectionClass.getSimpleName());
			return null;
		}
		
		final P projection = ObjectUtils.newInstance(projectionClass);
		
		queue.push(projection);

		final ContextObjects context = ContextObjects.of(objects);
		
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

	@Override
	public Object[] decompose(P projection) throws ProjectionError {
		return decompose(projection, ContextObjects.of());
	}

	@Override
	public Object[] decompose(P projection, ContextObjects context) throws ProjectionError {
		
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
	
	@SuppressWarnings("unchecked")
	@Override
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
		
		return (S)Arrays
					.stream(objects)
					.filter(sourceObjectClass::isInstance)
					.findFirst()
					.orElse(null);

	}
	
	public void setProperties(ProjectionProperty[] properties) {
		this.properties = properties;
	}
	
	public ProjectionProperty[] getProperties() {
		return properties;
	}
}
