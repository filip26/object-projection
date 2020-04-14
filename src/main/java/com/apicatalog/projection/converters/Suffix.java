package com.apicatalog.projection.converters;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;

public final class Suffix implements Converter<String, String> {

	String suffixString;
	
	@Override
	public void initConverter(final ConverterConfig ctx) {
		this.suffixString = Stream.of(ctx.getValues()).map(Object::toString).collect(Collectors.joining());
	}
	
	@Override
	public String forward(final String object) {

		if (StringUtils.isBlank(suffixString)) {
			return object;
		}
		
		return object.concat(suffixString);
	}

	@Override
	public String backward(final String object) {

		if (StringUtils.isBlank(suffixString)) {
			return object;
		}

		return object.substring(0, object.length() - suffixString.length());
	}
}
