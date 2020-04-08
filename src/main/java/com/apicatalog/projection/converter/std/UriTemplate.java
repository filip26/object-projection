package com.apicatalog.projection.converter.std;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.uritemplate.MalformedUriTemplate;
import com.apicatalog.uritemplate.UriTemplateL1;

public class UriTemplate implements Converter<String[], String> {

	UriTemplateL1 template;

	@Override
	public void initConverter(ConverterConfig ctx) throws ConverterError {
		try {
			init(ctx.getValues());
		} catch (MalformedUriTemplate e) {
			throw new ConverterError(e);
		}
	}

	@Override
	public String forward(String[] objects) throws ConverterError {
		return template.expand(objects);
	}

	@Override
	public String[] backward(String object) throws ConverterError {
		return template.extract(object);		
	}
	
	void init(String[] values) throws MalformedUriTemplate {
		this.template = UriTemplateL1.of(Arrays.stream(values).collect(Collectors.joining()));			
	}
}
