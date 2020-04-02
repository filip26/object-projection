package com.apicatalog.projection.reducer;

public class ReducerError extends Exception {

	private static final long serialVersionUID = 1069104973172217603L;

	public ReducerError(String message) {
		super(message);
	}
	
	public ReducerError(Throwable cause) {
		super(cause);
	}
	
}
