package com.apicatalog.projection;

public class ExtractionError extends Exception {

	private static final long serialVersionUID = -8097013988907088043L;

	public ExtractionError(String message) {
		super(message);
	}

	public ExtractionError(Throwable throwable) {
		super(throwable);
	}

	public ExtractionError(String message, Throwable throwable) {
		super(message, throwable);
	}

}
