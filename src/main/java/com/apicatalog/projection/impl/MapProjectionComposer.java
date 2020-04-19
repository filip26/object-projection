package com.apicatalog.projection.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionComposer;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.source.SourceType;

public final class MapProjectionComposer implements ProjectionComposer<Map<String, Object>> {
	
	final Logger logger = LoggerFactory.getLogger(MapProjectionComposer.class);
	
	final PropertyWriter[] writers;
	
	protected MapProjectionComposer(final PropertyWriter[] writers) {
		this.writers = writers;
	}
	
	public static final MapProjectionComposer newInstance(final PropertyWriter[] writers) {
		return new MapProjectionComposer(writers);
	}

	@Override
	public Map<String, Object> compose(ProjectionStack stack, CompositionContext context) throws ProjectionError {
		
		if (stack == null || context == null || writers == null) {
			throw new IllegalArgumentException();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Compose Map of {} object(s), {} properties, depth = {}", context.size(), writers.length, stack.length());
		
			if (logger.isTraceEnabled()) {
				context.stream().forEach(sourceObject -> logger.trace("  {}", sourceObject));
			}
		}

//TODO		// check for cycles
//		if (stack.contains(projectionClass)) {
//			logger.debug("Ignored. Projection {} is in processing already", projectionClass.getSimpleName());
//			return null;
//		}
		
		final Map<String, Object> projection = new HashMap<>(); 
		
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
		return Collections.emptySet();
	}
}
