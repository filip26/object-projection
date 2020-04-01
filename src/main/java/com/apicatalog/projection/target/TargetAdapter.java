package com.apicatalog.projection.target;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface TargetAdapter {

	Object forward(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError;

	Object backward(Object object, ContextObjects context) throws ProjectionError;

}
