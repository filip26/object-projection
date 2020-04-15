package com.apicatalog.projection.builder.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.conversion.Conversion;

public final class SourcesConversionApi<P, S, T> {
	
	final Logger logger = LoggerFactory.getLogger(SourcesConversionApi.class);
	
	final ConversionMappingBuilder builder;

	final SourcesPropertyApi<P> parent;
	
	protected SourcesConversionApi(ConversionMappingBuilder builder, SourcesPropertyApi<P> parent) {
		this.builder = builder;
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public SourcesPropertyApi<P> forward(Conversion<S, T> conversion) {
		builder.forward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public SourcesPropertyApi<P> backward(Conversion<T, S> conversion) {
		builder.backward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public SourcesPropertyApi<P> invertible(Conversion<S, T> forward, Conversion<T, S> backward) {
		builder.forward((Conversion<Object, Object>) forward);
		builder.backward((Conversion<Object, Object>) backward);
		return parent;
	}
}
