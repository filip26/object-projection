package com.apicatalog.projection.impl;

import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.property.PropertyWriter;

public final class ObjectProjectionComposer<P> extends AbstractProjectionComposer<P> {
	
	final Class<P> projectionClass;
	
	protected ObjectProjectionComposer(final Class<P> projectionClass, final PropertyWriter[] writers) {
		super(projectionClass.getCanonicalName(), writers);
		this.projectionClass = projectionClass;
	}
	
	public static final <A> ObjectProjectionComposer<A> newInstance(final Class<A> projectionClass, final PropertyWriter[] writers) {
		return new ObjectProjectionComposer<>(projectionClass, writers);
	}
	
	protected final P newInstance() {
		return ObjectUtils.newInstance(projectionClass);
		
	}
}
