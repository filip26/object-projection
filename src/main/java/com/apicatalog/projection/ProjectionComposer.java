package com.apicatalog.projection;

import java.util.Collection;

import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.source.SourceType;

public interface ProjectionComposer<P> {

	P compose(ProjectionStack stack, CompositionContext context) throws CompositionError;
	
	Collection<SourceType> getSourceTypes();

	Collection<String> getDependencies();
}
