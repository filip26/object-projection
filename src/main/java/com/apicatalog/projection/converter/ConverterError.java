package com.apicatalog.projection.converter;

import com.apicatalog.projection.object.ObjectType;

public class ConverterError extends Exception {

	private static final long serialVersionUID = 8321576342843307811L;
	
	public ConverterError(String message) {
		super(message);
	}
	
	public ConverterError(Throwable cause) {
		super(cause);
	}

	public static final ConverterError unconvertable(final ObjectType sourceType, final ObjectType targetType) {
		return new ConverterError("Can not convert " + sourceType + " to " + targetType);
	}
}
