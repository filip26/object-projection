package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.target.TargetAdapter;

public class ProvidedObjectProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(ProvidedObjectProperty.class);

	Getter targetGetter;
	Setter targetSetter;
	
	TargetAdapter targetAdapter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;	
	
	@Override
	public void forward(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
		if (targetSetter == null) {
			return;
		}
		
		logger.debug("Forward {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), objectQualifier, optional, queue.length());
		
		Object object = context.get(objectQualifier, targetSetter.getType().getObjectClass());
		
		if (object == null) {
			return;
		}
		
		if (targetAdapter != null) {
			object = targetAdapter.forward(queue, object, context);
		}
		
		targetSetter.set(queue.peek(), object);
	}

	@Override
	public void backward(ProjectionStack queue, ExtractionContext context) throws ProjectionError {
		
		if (targetGetter == null) {
			return;
		}

		logger.debug("Backward {} : {}, qualifier = {}, optional = {}, depth = {}", targetGetter.getName(), targetGetter.getType(), objectQualifier, optional, queue.length());

		Optional<Object> object = targetGetter.get(queue.peek());
		
		if (object.isEmpty()) {
			return;
		}
		
		if (targetAdapter != null) {
			object = Optional.ofNullable(targetAdapter.backward(object.get(), context));
		}
		
		if (object.isPresent() ) {
			context.set(objectQualifier, object.get());
		}
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
	
	public void setObjectQualifier(String objectQualifier) {
		this.objectQualifier = objectQualifier;
	}
	
	public void setTargetAdapter(TargetAdapter targetAdapter) {
		this.targetAdapter = targetAdapter;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}