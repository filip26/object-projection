package com.apicatalog.projection.conversion;

import com.apicatalog.projection.object.ObjectType;

public class ConversionNotFound extends Throwable {

	private static final long serialVersionUID = -7159105646784643063L;

	final ObjectType source;
	final ObjectType target;

	public ConversionNotFound(final ObjectType source, final ObjectType target) {
		super("Can not convert " + source + " to " + target + ". Please set explicit conversion.");
		this.source = source;
		this.target = target;
	}
	
	public ObjectType getSource() {
		return source;
	}
	
	public ObjectType getTarget() {
		return target;
	}
}
