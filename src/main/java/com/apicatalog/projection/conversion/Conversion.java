package com.apicatalog.projection.conversion;

import com.apicatalog.projection.converter.ConverterError;

public interface Conversion {

	Object convert(Object object) throws ConverterError;

}
