package com.apicatalog.projection.converter.std;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;

public class Concatenation implements Converter<String[], String> {

	@Override
	public void initConverter(ConverterConfig ctx) throws ConverterError {
		// no configuration
	}

	@Override
	public String forward(String[] objects) throws ConverterError {
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
	public String[] backward(String object) throws ConverterError {
		return new String[0];	// unsupported ->  return an empty array
	}
}
