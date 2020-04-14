package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.setter.Setter;

public class ProvidedProjectionPropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(ProvidedProjectionPropertyWriter.class);

	final ProjectionRegistry factory;
	
	Setter targetSetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;
	
	public ProvidedProjectionPropertyWriter(ProjectionRegistry factory) {
		this.factory = factory;
	}
	
	@Override
	public void write(ProjectionStack stack, CompositionContext context) throws ProjectionError {

		if (targetSetter == null) {
			return;
		}
		
		logger.debug("Write {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), objectQualifier, optional, stack.length());

		final CompositionContext clonedContext = new CompositionContext(context);
		
		Optional.ofNullable(objectQualifier).ifPresent(clonedContext::namespace);
		
		final Projection<?> projection = factory.get(targetSetter.getType().getType()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetSetter.getType().getType() +  " is not present.");
		}
			
		final Object object = projection.compose(stack, clonedContext);
		
		if (object != null) {
			targetSetter.set(stack.peek(), object);
		}
	}

	public void setTargetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
	}
	
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setObjectQualifier(String objectQualifier) {
		this.objectQualifier = objectQualifier;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}