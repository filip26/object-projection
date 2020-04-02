package com.apicatalog.projection.target;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;

public class TargetProjectionConverter implements TargetAdapter {

	final Logger logger = LoggerFactory.getLogger(TargetProjectionConverter.class);

	final ProjectionFactory factory;
	
	final Class<?> sourceClass;
	
	final Class<?> sourceComponentClass;

	final Class<?> targetClass;
	
	final Class<?> targetComponentClass;
	
	public TargetProjectionConverter(ProjectionFactory factory, Class<?> sourceClass, Class<?> sourceComponentClass, Class<?> targetClass, Class<?> targetComponentClass) {
		this.factory = factory;
		
		this.sourceClass = sourceClass;
		this.sourceComponentClass = sourceComponentClass;
		
		this.targetClass = targetClass;
		this.targetComponentClass = targetComponentClass;
	}
	
	@Override
	public Object forward(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError {
		
		logger.debug("Convert {} to {}, depth = {}, reference = true", sourceClass != null ? sourceClass.getSimpleName() : "unknown", targetClass.getSimpleName(), queue.length());

		final ContextObjects clonedSources = new ContextObjects(context);
		
		Optional.ofNullable(object).ifPresent(v -> clonedSources.addOrReplace(v, null));

		final Projection<?> projection = factory.get(targetClass); 
		
		if (projection != null) {
			return projection.compose(queue, clonedSources.getValues());
		}
		
		throw new ProjectionError("Projection " + targetClass.getCanonicalName() +  " is not present.");
	}

	@Override
	public Object backward(Object object, ContextObjects context) throws ProjectionError {
		logger.debug("Convert {} to {}, reference = true", targetClass.getSimpleName(), sourceClass != null ? sourceClass.getSimpleName() : "unknown");
		
		final Projection<Object> projection = (Projection<Object>) factory.get(targetClass); 
		
		if (projection != null) {
			return filter(projection.decompose(object, new ContextObjects(context)), context);
		}

		throw new ProjectionError("Projection " + targetClass.getCanonicalName() +  " is not present.");
	}
	
	Object filter(Object[] objects, ContextObjects context) {
		if (objects == null) {
			return null;
		}

		Optional<Object> value = Optional.empty();

		for (Object object : objects) {

			if (value.isEmpty() 
					&& (sourceComponentClass != null ? sourceComponentClass.isInstance(object) : sourceClass.isInstance(object))
				) {
				
				value = Optional.ofNullable(object);
				
			} else if (!context.contains(object.getClass(), null)) {
				context.addOrReplace(object, null);
				
			}
		}
		return value.orElse(null);
	}
}
