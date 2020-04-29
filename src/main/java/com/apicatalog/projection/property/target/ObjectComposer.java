package com.apicatalog.projection.property.target;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;

public final class ObjectComposer implements TargetComposer {

	final Logger logger = LoggerFactory.getLogger(ObjectComposer.class);

	final String projectionName;
	
	ProjectionComposer<?> composer;
	
	public ObjectComposer(final String projectionName) {
		this.projectionName = projectionName;
	}
	
	@Override
	public Optional<Object> compose(final ProjectionStack stack, final Object object, final CompositionContext context) throws CompositionError {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Compose {} of {}, reference = true", projectionName, object.getClass().getSimpleName());
		}
		
		if (composer == null) {
			throw new CompositionError("Projection " + projectionName +  " is not set.");
		}

		final CompositionContext clonedSources = new CompositionContext(context);
		
		Optional.ofNullable(object).ifPresent(clonedSources::put);

		return Optional.ofNullable(composer.compose(stack, clonedSources));		
	}
	
	public void setProjection(final Projection<?> projection) {
		this.composer = projection.getComposer().orElse(null);
	}

	@Override
	public String getProjectionName() {
		return projectionName;
	}
}
