package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConvertorError;

public interface ConversionMapping {

	Object forward(Object value) throws ConvertorError, ProjectionError;
	
	Object backward(Object value) throws ConvertorError, ProjectionError;

}
