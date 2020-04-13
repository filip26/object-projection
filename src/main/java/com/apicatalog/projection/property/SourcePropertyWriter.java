package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.target.TargetWriter;

public final class SourcePropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyWriter.class);

	final SourceReader sourceReader;
	
	final TargetWriter targetWriter;
	
	Set<Integer> visibleLevels;

	public SourcePropertyWriter(final SourceReader sourceReader, final TargetWriter targetWriter) {
		this.sourceReader = sourceReader;
		this.targetWriter = targetWriter;
	}

	@Override
	public void write(final ProjectionStack queue, final CompositionContext context) throws ProjectionError {

		if (sourceReader == null) {
			logger.warn("Source reader is missing. Property skipped.");
			return;
		}

		if (targetWriter == null) {
			logger.warn("Target writer is missing. Property skipped.");
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Write {} to {}, depth = {}", sourceReader.getType(), targetWriter.getType(), queue.length());
		}

		// get source value
		final Optional<Object> object = sourceReader.read(queue, context);
		
		if (object.isPresent()) {
			targetWriter.write(queue, context, object.get());
		}

//		object = Optional.ofNullable(targetAdapter.forward(queue, object.get(), context));
//		
//		if (object.isPresent()) {
//			if (logger.isTraceEnabled()) {
//				logger.trace("{} : {} = {}", targetSetter.getName(), targetSetter.getType(), object.get());	
//			}
//			
//			targetSetter.set(queue.peek(), object.get());
//		}
	}

	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}	
}
