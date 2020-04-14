package com.apicatalog.projection.conversion;

import com.apicatalog.projection.converter.ConverterError;

@FunctionalInterface
public interface Conversion {

	Object convert(Object object) throws ConverterError;

}
