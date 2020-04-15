package com.apicatalog.projection.api;

import com.apicatalog.projection.converter.Converter;

public interface ConversionApi<P> {

	P conversion(Class<? extends Converter<?, ?>> converter, String...params);
	
	<S, T> LambdaConversionApi<P, S, T> conversion(Class<? extends S> source, Class<? extends T> target);
}
