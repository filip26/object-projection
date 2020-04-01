package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface TargetMapping {

	Class<?> getSourceClass();
	Class<?> getSourceComponentClass();

	Class<?> getTargetClass();
	Class<?> getTargetComponentClass();
	
	Object construct(ProjectionQueue path, Object object, ContextObjects context) throws ProjectionError;
	
	Object deconstruct(Object object, ContextObjects context) throws ProjectionError;
	
}
