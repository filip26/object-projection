package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.beans.Getter;
import com.apicatalog.projection.beans.Setter;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;

public class ProvidedProjectionProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(ProvidedProjectionProperty.class);

	final ProjectionFactory factory;
	
	Getter targetGetter;
	Setter targetSetter;
	
	Set<Integer> visibleLevels;
	
	String sourceObjectQualifier;
	
	boolean optional;
	
	public ProvidedProjectionProperty(ProjectionFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public void forward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {

		if (targetSetter == null) {
			return;
		}
		
		logger.debug("Forward {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), sourceObjectQualifier, optional, queue.length());

		final ContextObjects clonedSources = new ContextObjects(context);
		
		Optional.ofNullable(sourceObjectQualifier).ifPresent(clonedSources::pushNamespace);
		
		final Projection<?> projection = factory.get(targetSetter.getType().getObjectClass()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetSetter.getType().getObjectClass() +  " is not present.");
		}
			
		Object object = projection.compose(queue, clonedSources.getValues());
		
		if (object != null) {
			targetSetter.set(queue.peek(), object);
		}
	}

	@Override
	public void backward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetGetter.getType().getObjectClass()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetGetter.getType().getObjectClass() +  " is not present.");			
		}
		
		Object object = targetGetter.get(queue.peek());
		
		projection.decompose(object, context);
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
}