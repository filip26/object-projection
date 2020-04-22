package com.apicatalog.projection.api;

public class ProjectionError extends Exception {

	private static final long serialVersionUID = 6092000819176194294L;

	public ProjectionError(String message) {
		super(message);
	}
	
	public ProjectionError(String message, Throwable e) {
		super(message, e);
	}
	
	/**
	 * 
	 * @deprecated use ProjectionError(String message, Throwable e)
	 */
	@Deprecated(since = "v0.8.12", forRemoval = true)
	public ProjectionError(Throwable e) {
		super(e);
	}
}
