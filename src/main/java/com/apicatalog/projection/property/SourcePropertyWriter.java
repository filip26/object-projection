package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.target.Composer;

public final class SourcePropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyWriter.class);

	final SourceReader sourceReader;
	
	final Setter targetSetter;
	
	final Composer composer;
	
	Set<Integer> visibleLevels;

	public SourcePropertyWriter(final SourceReader sourceReader, final Setter targetSetter, final Composer composer) {
		this.sourceReader = sourceReader;
		this.targetSetter = targetSetter;
		this.composer = composer;
	}

	@Override
	public void write(final ProjectionStack queue, final CompositionContext context) throws ProjectionError {

		if (sourceReader == null) {
			logger.warn("Source reader is missing. Property skipped.");
			return;
		}

		if (targetSetter == null) {
			logger.warn("Target setter is missing. Property skipped.");
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Write {} to {}, depth = {}", sourceReader.getType(), targetSetter.getType(), queue.length());
		}

		// get source value
		Optional<Object> object = sourceReader.read(context);

		if (object.isEmpty()) {
			return;
		}

		if (composer != null) {
			object = composer.compose(queue, object.get(), context);
		}
		
		if (object.isPresent()) {
			if (logger.isTraceEnabled()) {
				logger.trace("{} : {} = {}", targetSetter.getName(), targetSetter.getType(), object.get());	
			}
			
			targetSetter.set(queue.peek(), object.get());
		}		
	}

	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}	
}
