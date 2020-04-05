package com.apicatalog.projection.converter.std;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.reducer.Reducer;
import com.apicatalog.projection.reducer.ReducerConfig;
import com.apicatalog.projection.reducer.ReducerError;
import com.apicatalog.uritemplate.MalformedUriTemplate;
import com.apicatalog.uritemplate.UriTemplateL1;

public class UriTemplate implements Reducer<String, String>, Converter<String, String> {

	UriTemplateL1 template;

	@Override
	public void initReducer(ReducerConfig ctx) throws ReducerError {
		try {
			init(ctx.getValues());
		} catch (MalformedUriTemplate e) {
			throw new ReducerError(e);
		}
	}

	@Override
	public String reduce(String[] objects) throws ReducerError {
		return template.expand(objects);
	}

	@Override
	public String[] expand(String object) throws ReducerError {
		return template.extract(object);
	}

	@Override
	public void initConverter(ConverterConfig ctx) throws ConverterError {
		try {
			init(ctx.getValues());
		} catch (MalformedUriTemplate e) {
			throw new ConverterError(e);
		}
	}

	@Override
	public String forward(String object) throws ConverterError {
		return template.expand(object);
	}

	@Override
	public String backward(String object) throws ConverterError {
		
		final String[] variables = template.extract(object);
		
		if (variables == null || variables.length == 0) {
			return null;
		}
		
		// return just the first variable
		return variables[0];
	}
	
	void init(String[] values) throws MalformedUriTemplate {
		this.template = UriTemplateL1.of(Arrays.stream(values).collect(Collectors.joining()));			
	}
}
