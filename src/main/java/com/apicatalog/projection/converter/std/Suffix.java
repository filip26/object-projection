package com.apicatalog.projection.converter.std;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;

public class Suffix implements Converter<String, String> {

	String suffixString;
	
	@Override
	public void initConverter(final ConverterConfig ctx) {
		this.suffixString = Stream.of(ctx.getValues()).map(Object::toString).collect(Collectors.joining());
	}
	
	@Override
	public String forward(String object) {

		if (StringUtils.isBlank(suffixString)) {
			return object;
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(object);		
		builder.append(suffixString);
		
		return builder.toString();
	}

	@Override
	public String backward(String object) {

		if (StringUtils.isBlank(suffixString)) {
			return object;
		}
		
		int lengthToCut = suffixString.length();

		return object.substring(0, object.length() - lengthToCut);
	}
}
