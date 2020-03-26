package com.apicatalog.projection.converter;

public class ConverterError extends Exception {

	private static final long serialVersionUID = 8321576342843307811L;

	public ConverterError(String message) {
		super(message);
	}
	
	public ConverterError(Throwable cause) {
		super(cause);
	}
	
}
