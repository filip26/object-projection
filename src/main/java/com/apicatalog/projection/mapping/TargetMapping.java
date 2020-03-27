package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.SourceObjects;

public interface TargetMapping {

	Class<?> getTargetClass();
	Class<?> getItemClass();
	
	boolean isCollection();
	boolean isReference();
	
	Object construct(int level, Object object, SourceObjects sources) throws ProjectionError;
	
	Object[] deconstruct(Object object)  throws ProjectionError;
}
