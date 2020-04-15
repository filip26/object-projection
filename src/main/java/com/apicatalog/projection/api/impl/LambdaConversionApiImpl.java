package com.apicatalog.projection.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.conversion.Conversion;

public final class LambdaConversionApiImpl<P, S, T> implements LambdaConversionApi<P, S, T> {
	
	final Logger logger = LoggerFactory.getLogger(LambdaConversionApiImpl.class);
	
	final ConversionMappingBuilder builder;

	final P parent;
	
	protected LambdaConversionApiImpl(ConversionMappingBuilder builder, P parent) {
		this.builder = builder;
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public P forward(Conversion<S, T> conversion) {
		builder.forward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public P backward(Conversion<T, S> conversion) {
		builder.backward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public P invertible(Conversion<S, T> forward, Conversion<T, S> backward) {
		builder.forward((Conversion<Object, Object>) forward);
		builder.backward((Conversion<Object, Object>) backward);
		return parent;
	}
}
