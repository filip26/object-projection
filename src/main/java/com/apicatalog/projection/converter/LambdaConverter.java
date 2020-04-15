package com.apicatalog.projection.converter;

import com.apicatalog.projection.conversion.Conversion;

public class LambdaConverter<S, T> implements Converter<S, T> {

	final Conversion<S, T> forward;
	
	final Conversion<T, S> backward;
	
	public LambdaConverter(final Conversion<S, T> forward, final Conversion<T, S> backward) {
		this.forward = forward;
		this.backward = backward;
	}
	
	@Override
	public void initConverter(ConverterConfig ctx) throws ConverterError {
		/* not used */
	}

	@Override
	public T forward(S object) throws ConverterError {
		if (forward == null) {
			return null;	// unsupported, null stops processing
		}
		
		return forward.convert(object);
	}

	@Override
	public S backward(T object) throws ConverterError {
		if (backward == null) {
			return null;	// unsupported, null stops processing
		}
		
		return backward.convert(object);
	}
}
