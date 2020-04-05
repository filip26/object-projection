package com.apicatalog.projection.property;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ProjectionContext;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.target.TargetAdapter;

public class ProvidedObjectProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(ProvidedObjectProperty.class);

	Getter targetGetter;
	Setter targetSetter;
	
	TargetAdapter targetAdapter;
	
	Set<Integer> visibleLevels;
	
	String sourceObjectQualifier;
	
	boolean optional;	
	
	@Override
	public void forward(ProjectionQueue queue, ProjectionContext context) throws ProjectionError {
		
		if (targetSetter == null) {
			return;
		}
		
		logger.debug("Forward {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), sourceObjectQualifier, optional, queue.length());
		
		Object object = context.get(targetSetter.getType().getObjectClass(), sourceObjectQualifier);
		
		if (object == null) {
			return;
		}
		
		if (targetAdapter != null) {
			object = targetAdapter.forward(queue, object, context);
		}
		
		targetSetter.set(queue.peek(), object);
	}

	@Override
	public void backward(ProjectionQueue queue, ProjectionContext context) throws ProjectionError {
		
		if (targetGetter == null) {
			return;
		}

		logger.debug("Backward {} : {}, qualifier = {}, optional = {}, depth = {}", targetGetter.getName(), targetGetter.getType(), sourceObjectQualifier, optional, queue.length());

		Object object = targetGetter.get(queue.peek());

		if (object == null) {
			return;
		}
		
		if (targetAdapter != null) {
			object = targetAdapter.backward(object, context);
		}

		context.addOrReplace(object, sourceObjectQualifier);
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
	
	public void setVisible(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setSourceObjectQualifier(String sourceObjectQualifier) {
		this.sourceObjectQualifier = sourceObjectQualifier;
	}
	
	public void setTargetAdapter(TargetAdapter targetAdapter) {
		this.targetAdapter = targetAdapter;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}