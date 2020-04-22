package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.target.TargetComposer;
import com.apicatalog.projection.source.SourceType;

public final class SourcePropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyWriter.class);

	final SourceReader sourceReader;
	
	final Setter targetSetter;
	
	TargetComposer composer;
	
	Set<Integer> visibleLevels;

	protected SourcePropertyWriter(final SourceReader sourceReader, final Setter targetSetter) {
		this.sourceReader = sourceReader;
		this.targetSetter = targetSetter;
	}

	public static final SourcePropertyWriter newInstance(final SourceReader sourceReader, final Setter targetSetter) {
		
		if (targetSetter == null) {
			throw new IllegalArgumentException("Target setter is not set.");
		}

		if (sourceReader == null) {
			throw new IllegalArgumentException("Source reader is not set.");
		}
		
		return new SourcePropertyWriter(sourceReader, targetSetter);
	}
	
	@Override
	public void write(final ProjectionStack stack, final CompositionContext context) throws CompositionError {

		
		if (logger.isDebugEnabled()) {
			logger.debug("Write {} to {}, depth = {}", sourceReader.getTargetType(), targetSetter, stack.length());
		}

		// get source value
		Optional<Object> object = sourceReader.read(context);

		if (object.isEmpty()) {
			return;
		}
		
		// 

		if (composer != null) {
			object = composer.compose(stack, object.get(), context);
		}
		
		if (object.isPresent()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Writing {} to {}", object.get(), targetSetter);	
			}
			
			try {
				targetSetter.set(stack.peek(), object.get());
			} catch (ObjectError e) {
				throw new CompositionError("Can not set value " + object.get() + " to " + stack.peek().getClass().getCanonicalName());
			}
		}		
	}

	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setComposer(TargetComposer composer) {
		this.composer = composer;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceReader.getSourceTypes();
	}
	
	@Override
	public String getDependency() {
		return composer != null ? composer.getProjectionName() : null;
	}

	@Override
	public String getName() {
		return targetSetter.getName();
	}
}
