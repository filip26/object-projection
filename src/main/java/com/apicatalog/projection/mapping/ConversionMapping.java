package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;

@Deprecated
public interface ConversionMapping {

	Object forward(Object object) throws ProjectionError;
	
	Object backward(Object object) throws ProjectionError;

	ConverterMapping getConverterMapping();
	
}
