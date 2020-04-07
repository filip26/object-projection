package com.apicatalog.projection.property.target;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.CompositionContext;
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
	public Object forward(ProjectionQueue queue, Object object, CompositionContext context) throws ProjectionError {
		
		logger.debug("Convert {} to {}, depth = {}, reference = true", sourceType, targetType, queue.length());

		final CompositionContext clonedSources = new CompositionContext(context);
		
		Optional.ofNullable(object).ifPresent(v -> clonedSources.addOrReplace(v, null));

		final Projection<?> projection = factory.get(targetType.getObjectClass()); 
		
		if (projection != null) {
			return projection.compose(queue, clonedSources);
		}
		
		throw new ProjectionError("Projection " + targetType.getObjectClass() +  " is not present.");
	}

	@Override
	public Object backward(Object object, ExtractionContext context) throws ProjectionError {
		logger.debug("Convert {} to {}, reference = true", targetType, sourceType);
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getObjectClass()); 
		
		if (projection != null) {	//TODO			
			projection.extract(
							object, 
							context
								.accept(null, sourceType.getObjectClass(), sourceType.getObjectComponentClass())
							);
							
			return context.remove(null, sourceType.getObjectClass(), sourceType.getObjectComponentClass());
		}

		throw new ProjectionError("Projection " + targetType.getObjectClass().getCanonicalName() +  " is not present.");
	}
}
