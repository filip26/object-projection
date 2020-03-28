package com.apicatalog.projection.converter.std;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.ConverterError;

public class Prefix implements Converter<String, String> {

	String prefixString;
	
	@Override
	public void initConverter(final ConverterConfig ctx) {
		this.prefixString = Stream.of(ctx.getValues()).map(Object::toString).collect(Collectors.joining());
	}

	@Override
	public String forward(String object) throws ConverterError {
		
		if (StringUtils.isBlank(prefixString)) {
			return object;
		}
		
		StringBuilder builder = new StringBuilder();		
		builder.append(prefixString);
		builder.append(object);
				
		return builder.toString();
	}

	@Override
	public String backward(String object) throws ConverterError {
		
		if (StringUtils.isBlank(prefixString)) {
			return object;
		}
		
		int lengthToCut = prefixString.length();
		
		return object.substring(lengthToCut);
	}
}
