package com.apicatalog.projection.impl;

import java.util.Optional;

import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ObjectProjection<P> extends AbstractProjection<P> {
	
	final Class<P> projectionClass;

	protected ObjectProjection(final Class<P> projectionClass, final ProjectionComposer<P> composer, final ProjectionExtractor<P> extractor) {
		super(composer, extractor);
		this.projectionClass = projectionClass;
	}
	
	public static final <A> ObjectProjection<A> newInstance(final Class<A> projectionType, final PropertyReader[] readers, final PropertyWriter[] writers) {
		return new ObjectProjection<>(
						projectionType, 
						ProjectionComposerImpl.newInstance(projectionType.getCanonicalName(), projectionType, writers),
						ProjectionExtractorImpl.newInstance(projectionType.getCanonicalName(), readers)
						);
	}

	public final Optional<String> getName() {
		return Optional.of(projectionClass.getCanonicalName());
	}

	@Override
	public Class<?> getType() {
		return projectionClass;
	}
}
