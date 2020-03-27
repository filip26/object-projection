package com.apicatalog.projection.converter.std;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.Reducer;
import com.apicatalog.projection.converter.ReducerError;
import com.apicatalog.uritemplate.MalformedUriTemplate;
import com.apicatalog.uritemplate.UriTemplateL1;

public class UriTemplate implements Reducer, Converter {

	UriTemplateL1 template;

	@Override
	public void initReducer(ContextValue ctx) throws ReducerError {
		try {
			this.template = UriTemplateL1.of(ctx.getValues()[0]); 	//FIXME
			
		} catch (MalformedUriTemplate e) {
			throw new ReducerError(e);
		}
		
	}

	@Override
	public Object reduce(Object... objects) throws ReducerError {

		List<String> variables = new ArrayList<>();
		
		for (Object object : objects) {						//FIXME use implicit conversion
			if (object instanceof Collection) {
				for (Object item : (Collection<Object>)object) {
					variables.add(item.toString());	
				}
			} else {
				variables.add(object.toString());
			}
		}
				
		return template.expand(variables.toArray(new String[0]));
	}

	@Override
	public Object[] expand(Object object) throws ReducerError {
		return template.extract(object.toString());					//FIXME use implicit conversion
	}

	@Override
	public void initConverter(ContextValue ctx) throws ConverterError {
		try {
			this.template = UriTemplateL1.of(ctx.getValues()[0]); 	//FIXME
			
		} catch (MalformedUriTemplate e) {
			throw new ConverterError(e);
		}		
	}

	@Override
	public Object forward(Object object) throws ConverterError {
		return template.expand(object.toString());
	}

	@Override
	public Object backward(Object object) throws ConverterError {
		return template.extract(object.toString());
	}
}
