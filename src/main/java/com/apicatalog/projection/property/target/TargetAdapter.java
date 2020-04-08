package com.apicatalog.projection.property.target;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.context.CompositionContext;

public interface TargetAdapter {

	Object forward(ProjectionStack queue, Object object, CompositionContext context) throws ProjectionError;

	Object backward(Object object, ExtractionContext context) throws ProjectionError;

}
