package com.apicatalog.projection.api;

public interface AccessModeApi<P> {

	P readOnly();
	P readWrite();
	P writeOnly();
}
