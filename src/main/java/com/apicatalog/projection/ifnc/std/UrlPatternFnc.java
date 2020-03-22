package com.apicatalog.projection.ifnc.std;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.apicatalog.projection.ifnc.ContextValue;
import com.apicatalog.projection.ifnc.InvertibleFunction;
import com.apicatalog.projection.ifnc.InvertibleFunctionError;
import com.apicatalog.urlpattern.MalformedUrlPattern;
import com.apicatalog.urlpattern.UrlPattern;

public class UrlPatternFnc implements InvertibleFunction<String> {

	UrlPattern pattern;

	@Override
	public void init(ContextValue ctx) throws InvertibleFunctionError {
		try {
			this.pattern = UrlPattern.valueOf(ctx.getValues()[0]); 	//FIXME
			
		} catch (MalformedUrlPattern e) {
			throw new InvertibleFunctionError(e);
		}
		
	}

	@Override
	public String compute(Object... values) throws InvertibleFunctionError {

		//FIXME use implicit conversion
		List<String> v = Arrays.stream(values).map(Object::toString).collect(Collectors.toList());
		
		return pattern.populate(v.toArray(new String[0]));
	}

	@Override
	public Object[] inverse(String value) throws InvertibleFunctionError {
		// TODO Auto-generated method stub
		return new Object[] {value};
	}

	@Override
	public boolean isReverseable() {
		return true;
	}
	
	

}
