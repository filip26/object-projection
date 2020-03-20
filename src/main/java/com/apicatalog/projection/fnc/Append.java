package com.apicatalog.projection.fnc;

import com.apicatalog.projection.Value;

public class Append implements InvertibleFunction {

	@Override
	public Object compute(ContextValue ctx, Value... values) {

		StringBuilder builder = new StringBuilder();

		for (Value v : values) {
			builder.append(v.getObject().toString());
		}
		
		if (ctx.getValues() != null) {
			for (String v : ctx.getValues()) {
				builder.append(v);
			}
		}
		return builder.toString();
	}

	@Override
	public Object[] inverse(ContextValue ctx, Value value) {

		String string = value.getObject().toString();

		int lengthToCut = 0;
		
		if (ctx.getValues() != null) {
			for (String v : ctx.getValues()) {
				lengthToCut += v.length();
			}
		}
		
		if (lengthToCut > 0) {
			return new Object[] {string.substring(0, string.length() - lengthToCut)};
		}
		
		return new Object[] {value.getObject()};
	}
}
