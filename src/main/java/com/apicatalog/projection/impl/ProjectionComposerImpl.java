package com.apicatalog.projection.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.source.SourceType;

public final class ProjectionComposerImpl<P> implements ProjectionComposer<P> {
	
	final Logger logger = LoggerFactory.getLogger(ProjectionComposerImpl.class);

	final String projectionName;
	final Class<P> projectionType;
	
	final Collection<SourceType> sourceTypes;
	
	final Collection<String> dependecies;
	
	final PropertyWriter[] writers;
	
	protected ProjectionComposerImpl(final String projectionName, final Class<P> projectionType, final PropertyWriter[] writers) {
		this.projectionName = projectionName;
		this.projectionType = projectionType;
		this.writers = writers;
		this.sourceTypes = getComposableTypes(writers);
		this.dependecies = getDependencies(writers);
	}
	
	public static final <A> ProjectionComposer<A> newInstance(final String projectionName, final Class<A> projectionType, final PropertyWriter[] writers) {
		if (writers == null || writers.length == 0) {
			return null;
		}
		
		return new ProjectionComposerImpl<>(projectionName, projectionType, writers);
	}
	
	static final Collection<SourceType> getComposableTypes(final PropertyWriter[] writers) {
		return Arrays.stream(writers).map(PropertyWriter::getSourceTypes).flatMap(Collection::stream).collect(Collectors.toSet());
	}
	
	static final Collection<String> getDependencies(final PropertyWriter[] writers) {
		return Arrays.stream(writers).map(PropertyWriter::getDependency).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	@Override
	public P compose(ProjectionStack stack, CompositionContext context) throws ProjectionError {
		
		if (stack == null || context == null || writers == null) {
			throw new IllegalArgumentException();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Compose {} of {} object(s), {} properties, depth = {}", projectionName, context.size(), writers.length, stack.length());
		
			if (logger.isTraceEnabled()) {
				context.stream().forEach(sourceObject -> logger.trace("  {}", sourceObject));
			}
		}

		// check for cycles
		if (stack.contains(projectionName)) {
			logger.debug("Ignored. Projection {} is in processing already", projectionName);
			return null;
		}
		
		final P projection = newInstance();
		
		stack.push(projectionName, projection);

		for (PropertyWriter writer : writers) {
			// limit property visibility
			if (writer.isVisible(stack.length() - 1)) {
				writer.write(stack, context);				
			}
		}

		if (!projection.equals(stack.pop())) {
			throw new IllegalStateException();
		}
		
		return projection;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceTypes;
	}

	@Override
	public Collection<String> getDependencies() {
		return dependecies;
	}
	
	@SuppressWarnings("unchecked")
	protected P newInstance() {
		
		if (Map.class.isAssignableFrom(projectionType)) {
			return (P)new HashMap<String, Object>();
		}
		
		return ObjectUtils.newInstance(projectionType);
	}
}
