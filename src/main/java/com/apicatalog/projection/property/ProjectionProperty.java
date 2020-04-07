package com.apicatalog.projection.property;

import java.util.Set;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface ProjectionProperty {

	void forward(ProjectionQueue queue, CompositionContext context) throws ProjectionError;
	
	void backward(ProjectionQueue queue, ExtractionContext context) throws ProjectionError;

	boolean isVisible(int level);

	void setVisibility(Set<Integer> levels);
		
}
