package com.apicatalog.projection.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.source.SourceType;

public final class ObjectProjectionExtractor<P> implements ProjectionExtractor<P> {
	
	final Logger logger = LoggerFactory.getLogger(ObjectProjectionExtractor.class);

	final Collection<SourceType> sourceTypes;
	
	final PropertyReader[] readers;
	
	protected ObjectProjectionExtractor(final PropertyReader[] readers, final Collection<SourceType> sourceTypes) {
		this.readers = readers;
		this.sourceTypes = sourceTypes;
	}
	
	public static final <A> ObjectProjectionExtractor<A> newInstance(final PropertyReader[] readers) {
		return new ObjectProjectionExtractor<>(readers, getComposableTypes(readers));
	}

	static final Collection<SourceType> getComposableTypes(final PropertyReader[] readers) {
		final Set<SourceType> sourceTypes = new HashSet<>();
		
		for (PropertyReader reader : readers) {
			sourceTypes.addAll(reader.getSourceTypes());
		}
		
		
		return sourceTypes;
	}
	
	@Override
	public void extract(P projection, ExtractionContext context) throws ProjectionError {
		
		if (projection == null || context == null || readers == null) {
			throw new IllegalArgumentException();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Extract {} object(s) from {}, {} properties", context.size(), projection.getClass().getSimpleName(), readers.length);
		}

		final ProjectionStack stack = ProjectionStack.create().push(projection);
		
		for (PropertyReader reader : readers) {
			reader.read(stack, context);			
		}
		
		if (!projection.equals(stack.pop())) {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceTypes;
	}
}
