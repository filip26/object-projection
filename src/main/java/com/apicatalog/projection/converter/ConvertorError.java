package com.apicatalog.projection.converter;

public class ConvertorError extends Exception {

	private static final long serialVersionUID = 8321576342843307811L;

	public ConvertorError(String message) {
		super(message);
	}
	
	public ConvertorError(Throwable cause) {
		super(cause);
	}
	
}
