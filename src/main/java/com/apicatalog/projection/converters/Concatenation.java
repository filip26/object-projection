package com.apicatalog.projection.converters;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;

public final class Concatenation implements Converter<String[], String> {

	@Override
	public void initConverter(final ConverterConfig ctx) throws ConverterError {
		// no configuration
	}

	@Override
	public String forward(final String[] objects) throws ConverterError {
		if (objects == null || objects.length == 0) {
			return null;
		}
		
		final StringBuilder builder = new StringBuilder();

		for (String object : objects) {
			builder.append(object);
		}
	
		return builder.toString();
	}

	@Override
	public String[] backward(final String object) throws ConverterError {
		return new String[0];	// unsupported ->  return an empty array
	}
}
