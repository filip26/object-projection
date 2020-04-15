package com.apicatalog.projection.conversion;

import com.apicatalog.projection.converter.ConverterError;

@FunctionalInterface
public interface Conversion<S, T> {

	T convert(S object) throws ConverterError;

}
