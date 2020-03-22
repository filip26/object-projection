package com.apicatalog.projection.ifnc;

import org.apache.commons.lang3.StringUtils;

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
			builder.append(v.toString());
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
}
