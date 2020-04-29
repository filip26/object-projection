package com.apicatalog.projection.property.target;

import java.util.Optional;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;

public interface TargetComposer {

	Optional<Object> compose(ProjectionStack stack, Object object, CompositionContext context) throws CompositionError;

	String getProjectionName();

}
