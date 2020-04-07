package com.apicatalog.projection.property.target;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface TargetAdapter {

	Object forward(ProjectionQueue queue, Object object, CompositionContext context) throws ProjectionError;

	Object backward(Object object, ExtractionContext context) throws ProjectionError;

}
