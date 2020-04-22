package com.apicatalog.projection.object;

public class ObjectError extends Throwable {

	private static final long serialVersionUID = 7538197767824134543L;

	public ObjectError(final String message) {
		super(message);
	}
	
	public ObjectError(final String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
