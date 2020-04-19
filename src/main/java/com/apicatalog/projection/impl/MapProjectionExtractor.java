package com.apicatalog.projection.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.source.SourceType;

public final class MapProjectionExtractor implements ProjectionExtractor<Map<String, Object>> {
	
	final Logger logger = LoggerFactory.getLogger(MapProjectionExtractor.class);
	
	final PropertyReader[] readers;
	
	protected MapProjectionExtractor(final PropertyReader[] readers) {
		this.readers = readers;
	}
	
	public static final MapProjectionExtractor newInstance(final PropertyReader[] readers) {
		return new MapProjectionExtractor(readers);
	}

	@Override
	public void extract(Map<String, Object> projection, ExtractionContext context) throws ProjectionError {
		
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
		return Collections.emptySet();
	}
}
