package com.apicatalog.projection.converter;

public class InvertibleFunctionError extends Exception {

	private static final long serialVersionUID = 8321576342843307811L;

	public InvertibleFunctionError(String message) {
		super(message);
	}
	
	public InvertibleFunctionError(Throwable cause) {
		super(cause);
	}
	
}
