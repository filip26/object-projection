package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public interface TargetMapping {

	Class<?> getSourceClass();
	Class<?> getSourceComponentClass();

	Class<?> getTargetClass();
	Class<?> getTargetComponentClass();
	
	Object construct(Path path, Object object, ContextObjects context) throws ProjectionError;
	
	Object deconstruct(Path path, Object object, ContextObjects context) throws ProjectionError;
	
}
