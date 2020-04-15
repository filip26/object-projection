package com.apicatalog.projection.api;

public interface SourceApi<P> {

	P source(final Class<?> sourceClass);

	P source(final Class<?> sourceClass, final String sourceProperty);

}
