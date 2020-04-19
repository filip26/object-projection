package com.apicatalog.projection.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.Property;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.source.SourceType;

abstract class AbstractProjectionComposer<P> implements ProjectionComposer<P> {
	
	final Logger logger = LoggerFactory.getLogger(AbstractProjectionComposer.class);

	final String projectionName;
	final Collection<SourceType> sourceTypes;
	final PropertyWriter[] writers;
	
	protected AbstractProjectionComposer(final String projectionName, final PropertyWriter[] writers) {
		this.projectionName = projectionName;
		this.writers = writers;
		this.sourceTypes = getComposableTypes(writers);
	}
	
	static final Collection<SourceType> getComposableTypes(final Property[] writers) {
		return Arrays.stream(writers).map(Property::getSourceTypes).flatMap(Collection::stream).collect(Collectors.toSet());
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
		return Collections.emptySet();	//TODO
	}
	
	protected abstract P newInstance();
}
