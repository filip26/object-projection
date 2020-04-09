package com.apicatalog.projection.property;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.ProjectionAdapter;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.context.CompositionContext;

public class ConstantProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(ConstantProperty.class);

	String[] constants;
	
	ProjectionAdapter targetAdapter;
	
	Setter targetSetter;

	Set<Integer> visibleLevels;

	@Override
	public void forward(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		logger.debug("Forward constant = {}, depth = {}", constants, queue.length());
		
		targetSetter.set(queue.peek(), targetAdapter.forward(queue, constants, context));
	}

	@Override
	public void backward(ProjectionStack queue, ExtractionContext context) throws ProjectionError {
		logger.trace("Backward is ignored for a constant");
		// nothing to do, it's a constant
	}

	public void setConstants(String[] constants) {
		this.constants = constants;
	}
	
	public void setTargetAdapter(ProjectionAdapter targetAdapter) {
		this.targetAdapter = targetAdapter;
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
}
