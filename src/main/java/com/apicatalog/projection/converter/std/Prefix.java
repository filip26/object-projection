package com.apicatalog.projection.converter.std;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.Converter;

public class Prefix implements Converter {

	String prefix;
	
	@Override
	public void initConverter(final ContextValue ctx) {
		this.prefix = Stream.of(ctx.getValues()).map(Object::toString).collect(Collectors.joining());
	}
	
	@Override
	public Object forward(Object object) {

		StringBuilder builder = new StringBuilder();
		
		if (StringUtils.isNotBlank(prefix)) {
			builder.append(prefix);
		}

		//TODO use implicit conversion -> flatMap
		if (object instanceof Collection) {
			for (Object vi : (Collection<Object>)object) {
				builder.append(vi.toString());
			}
		} else {
		
			builder.append(object.toString());
		}
				
		return builder.toString();
	}

	@Override
	public Object backward(Object object) {

		int lengthToCut = (prefix != null ? prefix.length() : 0);
				
		if (lengthToCut > 0) {
			return object.toString().substring(lengthToCut);	//TODO use implicit conversion
		}
		
		return object;
	}
}
