package com.apicatalog.projection.converter.std;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.converter.InvertibleFunction;
import com.apicatalog.uritemplate.MalformedUriTemplate;
import com.apicatalog.uritemplate.UriTemplateL1;

public class UrlPatternFnc implements InvertibleFunction<String> {

	UriTemplateL1 pattern;

	@Override
	public void init(ContextValue ctx) throws ConvertorError {
		try {
			this.pattern = UriTemplateL1.of(ctx.getValues()[0]); 	//FIXME
			
		} catch (MalformedUriTemplate e) {
			throw new ConvertorError(e);
		}
		
	}

	@Override
	public String compute(Object... values) throws ConvertorError {

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
				
		return pattern.expand(v.toArray(new String[0]));
	}

	@Override
	public Object[] inverse(String value) throws ConvertorError {
		// TODO Auto-generated method stub
		return new Object[] {value};
	}

	@Override
	public boolean isReverseable() {
		return true;
	}
	
	

}
