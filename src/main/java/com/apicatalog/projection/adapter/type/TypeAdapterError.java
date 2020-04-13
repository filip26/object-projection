package com.apicatalog.projection.adapter.type;

@Deprecated
public class TypeAdapterError extends Exception {

	private static final long serialVersionUID = 4772036729476568020L;

	public TypeAdapterError(String message) {
		super(message);
	}

	public TypeAdapterError(Throwable cause) {
		super(cause);
	}
	
	public TypeAdapterError(String message, Throwable cause) {
		super(message, cause);
	}
}
