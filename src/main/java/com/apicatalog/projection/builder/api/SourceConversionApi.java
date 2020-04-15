package com.apicatalog.projection.builder.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.conversion.Conversion;

public final class SourceConversionApi<P, S, T> {
	
	final Logger logger = LoggerFactory.getLogger(SourceConversionApi.class);
	
	final ConversionMappingBuilder builder;

	final SourcePropertyApi<P> parent;
	
	protected SourceConversionApi(ConversionMappingBuilder builder, SourcePropertyApi<P> parent) {
		this.builder = builder;
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public SourcePropertyApi<P> forward(Conversion<S, T> conversion) {
		builder.forward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public SourcePropertyApi<P> backward(Conversion<T, S> conversion) {
		builder.backward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public SourcePropertyApi<P> invertible(Conversion<S, T> forward, Conversion<T, S> backward) {
		builder.forward((Conversion<Object, Object>) forward);
		builder.backward((Conversion<Object, Object>) backward);
		return parent;
	}
}
