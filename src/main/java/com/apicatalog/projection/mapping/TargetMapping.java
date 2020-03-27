package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;

public interface TargetMapping {

	Class<?> getTargetClass();
	Class<?> getItemClass();
	
	boolean isCollection();
	boolean isReference();
	
	Object construct(int level, Object object, ContextObjects context) throws ProjectionError;
	
	Object[] deconstruct(Object object)  throws ProjectionError;
}
