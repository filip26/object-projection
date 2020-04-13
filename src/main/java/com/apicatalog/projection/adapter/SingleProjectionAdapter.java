package com.apicatalog.projection.adapter;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectType;

@Deprecated
public class SingleProjectionAdapter {

	final Logger logger = LoggerFactory.getLogger(SingleProjectionAdapter.class);

	final ProjectionRegistry factory;
	
	final ObjectType targetType;
		
	public SingleProjectionAdapter(ProjectionRegistry factory, ObjectType targetType) {
		this.factory = factory;
		this.targetType = targetType;
	}
	
	
	public Object forward(ProjectionStack stack, Object object, CompositionContext context) throws ProjectionError {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Convert {} to {}, depth = {}, reference = true", object.getClass().getSimpleName(), targetType, stack.length());
		}

		final Projection<?> projection = 
				Optional.ofNullable(factory.get(targetType.getType()))
						.orElseThrow(() -> new ProjectionError("Projection " + targetType.getType().getCanonicalName() +  " is not present."))
						;

		final CompositionContext clonedSources = new CompositionContext(context);
		
		Optional.ofNullable(object).ifPresent(clonedSources::put);

		return projection.compose(stack, clonedSources);
	}

	
	public Object backward(ObjectType sourceType, Object object, ExtractionContext context) throws ProjectionError {
		
		logger.debug("Convert {} to {}, reference = true", targetType, sourceType);
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = 
					Optional.ofNullable((Projection<Object>) factory.get(targetType.getType()))
							.orElseThrow(() -> new ProjectionError("Projection " + targetType.getType().getCanonicalName() +  " is not present."))
							;
			
		projection.extract(
						object, 
						context
							.accept(null, sourceType.getType(), sourceType.getComponentType())
						);
						
		return context.remove(null, sourceType.getType(), sourceType.getComponentType()).orElse(null);
	}
}
