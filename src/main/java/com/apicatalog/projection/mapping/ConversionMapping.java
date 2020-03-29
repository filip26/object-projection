package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;

public interface ConversionMapping {

	Object forward(Object object) throws ProjectionError;
	
	Object backward(Object object) throws ProjectionError;

	Class<?> getSourceClass();
	Class<?> getTargetClass();
	
}
