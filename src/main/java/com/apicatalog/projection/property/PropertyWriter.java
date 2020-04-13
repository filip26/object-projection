package com.apicatalog.projection.property;

import java.util.Set;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;

public interface PropertyWriter {

	void write(ProjectionStack stack, CompositionContext context) throws ProjectionError;
	
	boolean isVisible(int level);

	void setVisibility(Set<Integer> levels);
		
}
