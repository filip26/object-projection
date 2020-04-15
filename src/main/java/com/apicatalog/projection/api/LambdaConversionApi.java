package com.apicatalog.projection.api;

import com.apicatalog.projection.conversion.Conversion;

public interface LambdaConversionApi<P, S, T>  {

	P forward(Conversion<S, T> conversion);
	P backward(Conversion<T, S> conversion);
	
	P invertible(Conversion<S, T> forward, Conversion<T, S> backward);
	
}
