package com.apicatalog.projection.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.Property;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.source.SourceType;

public final class ProjectionExtractorImpl<P> implements ProjectionExtractor<P> {
	
	final Logger logger = LoggerFactory.getLogger(ProjectionExtractorImpl.class);

	final Collection<SourceType> sourceTypes;
	
	final String projectionName;
	
	final PropertyReader[] readers;
	
	protected ProjectionExtractorImpl(final String projectionName, final PropertyReader[] readers) {
		this.projectionName = projectionName;
		this.readers = readers;
		this.sourceTypes = getSourceTypes(readers);
	}

	public static <P> ProjectionExtractor<P> newInstance(final String projectionName, final PropertyReader[] readers) {
		return new ProjectionExtractorImpl<>(projectionName, readers);
	}

	static final Collection<SourceType> getSourceTypes(final PropertyReader[] readers) {		
		return Arrays.stream(readers).map(Property::getSourceTypes).flatMap(Collection::stream).collect(Collectors.toSet());
	}
	
	@Override
	public void extract(P projection, ExtractionContext context) throws ProjectionError {
		
		if (projection == null || context == null || readers == null) {
			throw new IllegalArgumentException();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Extract {} object(s) from {}, {} properties", context.size(), projection.getClass().getSimpleName(), readers.length);
		}

		final ProjectionStack stack = ProjectionStack.create().push(projectionName, projection);
		
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