package com.apicatalog.projection.property.target;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ProjectionContext;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface TargetAdapter {

	Object forward(ProjectionQueue queue, Object object, ProjectionContext context) throws ProjectionError;

	Object backward(Object object, ProjectionContext context) throws ProjectionError;

}
