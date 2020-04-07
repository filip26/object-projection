package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;

public class ProvidedProjectionProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(ProvidedProjectionProperty.class);

	final ProjectionRegistry factory;
	
	Getter targetGetter;
	Setter targetSetter;
	
	Set<Integer> visibleLevels;
	
	String sourceObjectQualifier;
	
	boolean optional;
	
	public ProvidedProjectionProperty(ProjectionRegistry factory) {
		this.factory = factory;
	}
	
	@Override
	public void forward(ProjectionQueue queue, CompositionContext context) throws ProjectionError {

		if (targetSetter == null) {
			return;
		}
		
		logger.debug("Forward {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), sourceObjectQualifier, optional, queue.length());

		final CompositionContext clonedContext = new CompositionContext(context);
		
		Optional.ofNullable(sourceObjectQualifier).ifPresent(clonedContext::namespace);
		
		final Projection<?> projection = factory.get(targetSetter.getType().getObjectClass()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetSetter.getType().getObjectClass() +  " is not present.");
		}
			
		final Object object = projection.compose(queue, clonedContext);
		
		if (object != null) {
			targetSetter.set(queue.peek(), object);
		}
	}

	@Override
	public void backward(ProjectionQueue queue, ExtractionContext context) throws ProjectionError {
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetGetter.getType().getObjectClass()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetGetter.getType().getObjectClass() +  " is not present.");			
		}
		
		final Object object = targetGetter.get(queue.peek());
		
		projection.extract(object, context);
	}
	
	public void setTargetGetter(Getter targetGetter) {
		this.targetGetter = targetGetter;
	}
	
	public void setTargetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
	}
	
	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setSourceObjectQualifier(String sourceObjectQualifier) {
		this.sourceObjectQualifier = sourceObjectQualifier;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}