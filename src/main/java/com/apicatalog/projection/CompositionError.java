package com.apicatalog.projection;

public class CompositionError extends Exception {

	private static final long serialVersionUID = -8097013988907088043L;

	public CompositionError(String message) {
		super(message);
	}

	public CompositionError(Throwable throwable) {
		super(throwable);
	}

	public CompositionError(String message, Throwable throwable) {
		super(message, throwable);
	}
}
