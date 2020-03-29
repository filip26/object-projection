package com.apicatalog.projection.converter.std;

import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.Reducer;
import com.apicatalog.projection.converter.ReducerError;

public class Concatenation implements Reducer<String, String> {

	@Override
	public void initReducer(final ConverterConfig ctx) {
		// no configuration
	}

	@Override
	public String reduce(String[] objects) throws ReducerError {
		
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
	public String[] expand(String object) throws ReducerError {
		return new String[0];	// unsupported ->  return an empty array
	}
}
