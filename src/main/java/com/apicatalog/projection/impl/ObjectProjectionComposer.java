package com.apicatalog.projection.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.property.Property;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.source.SourceType;

public final class ObjectProjectionComposer<P> implements ProjectionComposer<P> {
	
	final Logger logger = LoggerFactory.getLogger(ObjectProjectionComposer.class);

	final Class<P> projectionClass;
	final Collection<SourceType> sourceTypes;
	final PropertyWriter[] writers;
	
	protected ObjectProjectionComposer(final Class<P> projectionClass, final PropertyWriter[] writers, final Collection<SourceType> sourceTypes) {
		this.projectionClass = projectionClass;
		this.writers = writers;
		this.sourceTypes = sourceTypes;
	}
	
	public static final <A> ObjectProjectionComposer<A> newInstance(final Class<A> projectionClass, final PropertyWriter[] writers) {
		return new ObjectProjectionComposer<>(projectionClass, writers, getComposableTypes(writers));
	}

	static final Collection<SourceType> getComposableTypes(final Property[] writers) {
		final Set<SourceType> sourceTypes = new HashSet<>();
		
		for (Property writer : writers) {
			sourceTypes.addAll(writer.getSourceTypes());
		}
		
		return sourceTypes;
	}

	@Override
	public P compose(ProjectionStack stack, CompositionContext context) throws ProjectionError {
		
		if (stack == null || context == null || writers == null) {
			throw new IllegalArgumentException();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Compose {} of {} object(s), {} properties, depth = {}", projectionClass.getSimpleName(), context.size(), writers.length, stack.length());
		
			if (logger.isTraceEnabled()) {
				context.stream().forEach(sourceObject -> logger.trace("  {}", sourceObject));
			}
		}

		// check for cycles
		if (stack.contains(projectionClass)) {
			logger.debug("Ignored. Projection {} is in processing already", projectionClass.getSimpleName());
			return null;
		}
		
		final P projection = ObjectUtils.newInstance(projectionClass);
		
		stack.push(projection);

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
}
