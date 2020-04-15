package com.apicatalog.projection.builder.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.conversion.Conversion;

public final class SourcesSourceConversionApi<P, S, T> {
	
	final Logger logger = LoggerFactory.getLogger(SourcesSourceConversionApi.class);
	
	final ConversionMappingBuilder builder;

	final SourcesApi<P> parent;
	
	protected SourcesSourceConversionApi(ConversionMappingBuilder builder, SourcesApi<P> parent) {
		this.builder = builder;
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public SourcesApi<P> forward(Conversion<S, T> conversion) {
		builder.forward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public SourcesApi<P> backward(Conversion<T, S> conversion) {
		builder.backward((Conversion<Object, Object>) conversion);		
		return parent;
	}

	@SuppressWarnings("unchecked")
	public SourcesApi<P> invertible(Conversion<S, T> forward, Conversion<T, S> backward) {
		builder.forward((Conversion<Object, Object>) forward);
		builder.backward((Conversion<Object, Object>) backward);
		return parent;
	}
}
