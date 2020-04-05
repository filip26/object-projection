package com.apicatalog.projection.property;

import java.util.Set;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ProjectionContext;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface ProjectionProperty {

	void forward(ProjectionQueue queue, ProjectionContext context) throws ProjectionError;
	
	void backward(ProjectionQueue queue, ProjectionContext context) throws ProjectionError;

	boolean isVisible(int level);

	void setVisible(Set<Integer> levels);
	
}
