package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;

public interface ConversionMapping {

	Object forward(Object value) throws ConverterError, ProjectionError;
	
	Object backward(Object value) throws ConverterError, ProjectionError;

}
