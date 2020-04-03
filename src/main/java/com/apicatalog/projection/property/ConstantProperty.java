package com.apicatalog.projection.property;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.target.TargetAdapter;

public class ConstantProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(ConstantProperty.class);

	String[] constants;
	
	TargetAdapter targetAdapter;
	
	Setter targetSetter;

	Set<Integer> visibleLevels;

	@Override
	public void forward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
		logger.debug("Forward constant = {}, depth = {}", constants, queue.length());
		
		targetSetter.set(queue.peek(), targetAdapter.forward(queue, constants, context));
	}

	@Override
	public void backward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
		// nothing to do, it's a constant
	}

	public void setConstants(String[] constants) {
		this.constants = constants;
	}
	
	public void setTargetAdapter(TargetAdapter targetAdapter) {
		this.targetAdapter = targetAdapter;
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
}
