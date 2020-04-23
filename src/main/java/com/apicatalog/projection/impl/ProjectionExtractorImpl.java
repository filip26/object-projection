package com.apicatalog.projection.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.source.SourceType;

public final class ProjectionExtractorImpl<P> implements ProjectionExtractor<P> {
	
	final Logger logger = LoggerFactory.getLogger(ProjectionExtractorImpl.class);

	final Collection<SourceType> sourceTypes;
	
	final Collection<String> dependecies;
	
	final String projectionName;
	
	final PropertyReader[] readers;
	
	protected ProjectionExtractorImpl(final String projectionName, final PropertyReader[] readers) {
		this.projectionName = projectionName;
		this.readers = readers;
		this.sourceTypes = getSourceTypes(readers);
		this.dependecies = getDependencies(readers);
	}

	public static <P> ProjectionExtractor<P> newInstance(final String projectionName, final PropertyReader[] readers) {
		if (readers == null || readers.length == 0) {
			return null;
		}
		
		return new ProjectionExtractorImpl<>(projectionName, readers);
	}

	static final Collection<SourceType> getSourceTypes(final PropertyReader[] readers) {		
		return Arrays.stream(readers).map(PropertyReader::getSourceTypes).flatMap(Collection::stream).collect(Collectors.toSet());
	}
	
	static final Collection<String> getDependencies(final PropertyReader[] readers) {
		return Arrays.stream(readers).map(PropertyReader::getDependency).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	
	@Override
	public void extract(P projection, ExtractionContext context) throws ExtractionError {
		
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

	@Override
	public Collection<String> getDependencies() {
		return dependecies;
	}

	@Override
	public String getProjectionName() {
		return projectionName;
	}
}