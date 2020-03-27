package com.apicatalog.projection.converter.std;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.Converter;

public class Append implements Converter {

	String suffix;
	
	@Override
	public void init(final ContextValue ctx) {
		this.suffix = Stream.of(ctx.getValues()).map(Object::toString).collect(Collectors.joining());
	}
	
	@Override
	public Object forward(Object object) {

		StringBuilder builder = new StringBuilder();
			//TODO use implicit conversion -> flatMap
			if (object instanceof Collection) {
				for (Object vi : (Collection<Object>)object) {
					builder.append(vi.toString());
				}
			} else {
			
				builder.append(object.toString());
			}
		
		if (StringUtils.isNotBlank(suffix)) {
			builder.append(suffix);
		}
		
		return builder.toString();
	}

	@Override
	public Object backward(Object object) {

		int lengthToCut = (suffix != null ? suffix.length() : 0);
				
		if (lengthToCut > 0) {
			return object.toString().substring(0, object.toString().length() - lengthToCut);	//TODO use implicit conversion
		}
		
		return object;
	}
}
