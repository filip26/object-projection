package com.apicatalog.projection.property.target;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.ProjectionContext;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ProjectionQueue;

public class TargetProjectionConverter implements TargetAdapter {

	final Logger logger = LoggerFactory.getLogger(TargetProjectionConverter.class);

	final ProjectionRegistry factory;
	
	final ObjectType sourceType;
	final ObjectType targetType;
		
	public TargetProjectionConverter(ProjectionRegistry factory, ObjectType sourceType, ObjectType targetType) {
		this.factory = factory;
		this.sourceType = sourceType;
		this.targetType = targetType;
	}
	
	@Override
	public Object forward(ProjectionQueue queue, Object object, ProjectionContext context) throws ProjectionError {
		
		logger.debug("Convert {} to {}, depth = {}, reference = true", sourceType, targetType, queue.length());

		final ProjectionContext clonedSources = new ProjectionContext(context);
		
		Optional.ofNullable(object).ifPresent(v -> clonedSources.addOrReplace(v, null));

		final Projection<?> projection = factory.get(targetType.getObjectClass()); 
		
		if (projection != null) {
			return projection.compose(queue, clonedSources);
		}
		
		throw new ProjectionError("Projection " + targetType.getObjectClass() +  " is not present.");
	}

	@Override
	public Object backward(Object object, ProjectionContext context) throws ProjectionError {
		logger.debug("Convert {} to {}, reference = true", targetType, sourceType);
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getObjectClass()); 
		
		if (projection != null) {
			return filter(projection.decompose(object, new ProjectionContext(context)), context);
		}

		throw new ProjectionError("Projection " + targetType.getObjectClass().getCanonicalName() +  " is not present.");
	}
	
	Object filter(Object[] objects, ProjectionContext context) {
		if (objects == null) {
			return null;
		}

		Optional<Object> value = Optional.empty();

		for (final Object object : objects) {

			if (value.isEmpty() && sourceType.isInstance(object)) {
				
				value = Optional.ofNullable(object);
				
			} else if (!context.contains(object.getClass(), null)) {
				context.addOrReplace(object, null);
				
			}
		}
		return value.orElse(null);
	}
}
