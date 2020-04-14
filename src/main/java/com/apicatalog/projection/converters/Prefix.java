package com.apicatalog.projection.converters;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;

public final class Prefix implements Converter<String, String> {

	String prefixString;
	
	@Override
	public void initConverter(final ConverterConfig ctx) {
		this.prefixString = Stream.of(ctx.getValues()).map(Object::toString).collect(Collectors.joining());
	}

	@Override
	public String forward(final String object) throws ConverterError {
		
		if (StringUtils.isBlank(prefixString)) {
			return object;
		}

		return prefixString.concat(object);
	}

	@Override
	public String backward(final String object) throws ConverterError {
		
		if (StringUtils.isBlank(prefixString)) {
			return object;
		}
		
		return object.substring(prefixString.length());
	}
}
