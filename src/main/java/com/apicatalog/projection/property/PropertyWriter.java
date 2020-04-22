package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Set;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.source.SourceType;

public interface PropertyWriter extends Property {

	void write(ProjectionStack stack, CompositionContext context) throws CompositionError;
	
	boolean isVisible(int level);

	void setVisibility(Set<Integer> levels);
	
	String getDependency();
	
	Collection<SourceType> getSourceTypes();
	
}
