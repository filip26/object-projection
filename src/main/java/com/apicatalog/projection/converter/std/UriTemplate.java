package com.apicatalog.projection.converter.std;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.InvertibleFunction;
import com.apicatalog.uritemplate.MalformedUriTemplate;
import com.apicatalog.uritemplate.UriTemplateL1;

public class UriTemplate implements InvertibleFunction<String> {

	UriTemplateL1 template;

	@Override
	public void init(ContextValue ctx) throws ConverterError {
		try {
			this.template = UriTemplateL1.of(ctx.getValues()[0]); 	//FIXME
			
		} catch (MalformedUriTemplate e) {
			throw new ConverterError(e);
		}
		
	}

	@Override
	public String compute(Object... values) throws ConverterError {

		//TODO again, flatMap 
		//FIXME use implicit conversion
		
		List<String> v = new ArrayList<>();
		
		for (Object o1 : values) {
			if (o1 instanceof Collection) {
				for (Object o2 : (Collection<Object>)o1) {
					v.add(o2.toString());	
				}
			} else {
				v.add(o1.toString());
			}
		}
				
		return template.expand(v.toArray(new String[0]));
	}

	@Override
	public Object[] inverse(String value) throws ConverterError {
		return template.extract(value);
	}

	@Override
	public boolean isReverseable() {
		return true;
	}
	
	

}
