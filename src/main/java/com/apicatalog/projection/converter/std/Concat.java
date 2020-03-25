package com.apicatalog.projection.converter.std;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.InvertibleFunction;

public class Concat implements InvertibleFunction<String> {

	String suffix;
	
	@Override
	public void init(final ContextValue ctx) {
		if (ctx.getValues() != null) {
			if (ctx.getValues().length > 1) {
				StringBuilder builder = new StringBuilder();

				for (String v : ctx.getValues()) {
					builder.append(v);
				}
			} else if (ctx.getValues().length == 1) {
				this.suffix = ctx.getValues()[0];
			}
		}
	}
	
	@Override
	public String compute(Object... objects) {

		StringBuilder builder = new StringBuilder();
		for (Object v : objects) {
			//TODO use implicit conversion -> flatMap
			if (v instanceof Collection) {
				for (Object vi : (Collection<Object>)v) {
					builder.append(vi.toString());
				}
			} else {
			
				builder.append(v.toString());
			}
		}
		if (StringUtils.isNotBlank(suffix)) {
			builder.append(suffix);
		}
		
		return builder.toString();
	}

	@Override
	public Object[] inverse(String value) {

		int lengthToCut = (suffix != null ? suffix.length() : 0);
				
		if (lengthToCut > 0) {
			return new Object[] {value.substring(0, value.length() - lengthToCut)};
		}
		
		return new Object[] {value};
	}

	@Override
	public boolean isReverseable() {
		return false;
	}
}
