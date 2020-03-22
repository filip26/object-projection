package com.apicatalog.projection.fnc;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Value;

public class Concat implements InvertibleFunction {

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
	public Object compute(Value... values) {

		StringBuilder builder = new StringBuilder();

		for (Value v : values) {
			builder.append(v.getObject().toString());
		}
		if (StringUtils.isNotBlank(suffix)) {
			builder.append(suffix);
		}
		
		return builder.toString();
	}

	@Override
	public Object[] inverse(Value value) {

		final String string = value.getObject().toString();

		int lengthToCut = (suffix != null ? suffix.length() : 0);
				
		if (lengthToCut > 0) {
			return new Object[] {string.substring(0, string.length() - lengthToCut)};
		}
		
		return new Object[] {value.getObject()};
	}
}
