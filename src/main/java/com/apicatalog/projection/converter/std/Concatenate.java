package com.apicatalog.projection.converter.std;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.Reducer;
import com.apicatalog.projection.converter.ReducerError;

public class Concatenate implements Reducer {

	String suffix;
	
	@Override
	public void initReducer(final ConverterConfig ctx) {
		this.suffix = Stream.of(ctx.getValues()).map(Object::toString).collect(Collectors.joining());		
	}

	@Override
	public Object reduce(Object... objects) throws ReducerError {
		StringBuilder builder = new StringBuilder();
		//TODO use implicit conversion -> flatMap
		for (Object object : objects) {
			if (object instanceof Collection) {
				for (Object vi : (Collection<Object>)object) {
					builder.append(vi.toString());
				}
			} else {
			
				builder.append(object.toString());
			}
		}
	
		if (StringUtils.isNotBlank(suffix)) {
			builder.append(suffix);
		}
		
		return builder.toString();
	}

	@Override
	public Object[] expand(Object object) throws ReducerError {
		// TODO Auto-generated method stub
		return null;
	}
}
