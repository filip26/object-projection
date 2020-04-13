package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;

@Deprecated
public class ProvidedProjectionProperty  {

	final Logger logger = LoggerFactory.getLogger(ProvidedProjectionProperty.class);

	final ProjectionRegistry factory;
	
	Getter targetGetter;
	Setter targetSetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;
	
	public ProvidedProjectionProperty(ProjectionRegistry factory) {
		this.factory = factory;
	}
	
	public void forward(ProjectionStack queue, CompositionContext context) throws ProjectionError {

		if (targetSetter == null) {
			return;
		}
		
		logger.debug("Forward {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), objectQualifier, optional, queue.length());

		final CompositionContext clonedContext = new CompositionContext(context);
		
		Optional.ofNullable(objectQualifier).ifPresent(clonedContext::namespace);
		
		final Projection<?> projection = factory.get(targetSetter.getType().getType()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetSetter.getType().getType() +  " is not present.");
		}
			
		final Object object = projection.compose(queue, clonedContext);
		
		if (object != null) {
			targetSetter.set(queue.peek(), object);
		}
	}

	public void backward(ProjectionStack queue, ExtractionContext context) throws ProjectionError {

		if (targetGetter == null) {
			return;
		}

		logger.debug("Backward {} : {}, qualifier = {}, optional = {}, depth = {}", targetGetter.getName(), targetGetter.getType(), objectQualifier, optional, queue.length());

		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetGetter.getType().getType()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetGetter.getType().getType() +  " is not present.");			
		}

		Optional.ofNullable(objectQualifier).ifPresent(context::addNamespace);

		final Optional<Object> object = targetGetter.get(queue.peek());

		if (object.isPresent()) {
			projection.extract(object.get(), context);
		}

		Optional.ofNullable(objectQualifier).ifPresent(s -> context.removeLastNamespace());
	}
	
	public void setTargetGetter(Getter targetGetter) {
		this.targetGetter = targetGetter;
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