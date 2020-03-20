package com.apicatalog.projection;

public class ProjectionError extends Exception {

	private static final long serialVersionUID = -8097013988907088043L;

	public ProjectionError(String message) {
		super(message);
	}

	public ProjectionError(Throwable throwable) {
		super(throwable);
	}

	public ProjectionError(String message, Throwable throwable) {
		super(message, throwable);
	}

}
