package com.apicatalog.projection.converters;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.uritemplate.MalformedUriTemplate;
import com.apicatalog.uritemplate.UriTemplateL1;

public final class UriTemplate implements Converter<String[], String> {

	UriTemplateL1 template;

	@Override
	public void initConverter(final ConverterConfig ctx) throws ConverterError {
		try {
			this.template = init(ctx.getValues());
			
		} catch (MalformedUriTemplate e) {
			throw new ConverterError(e);
		}
	}

	@Override
	public String forward(final String[] objects) throws ConverterError {
		return template.expand(objects);
	}

	@Override
	public String[] backward(final String object) throws ConverterError {
		return template.extract(object);		
	}
	
	static final UriTemplateL1 init(final String[] values) throws MalformedUriTemplate {
		return UriTemplateL1.of(Arrays.stream(values).collect(Collectors.joining()));			
	}
}
