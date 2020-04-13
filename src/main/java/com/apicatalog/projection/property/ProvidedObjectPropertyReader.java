package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.target.TargetReader;

public class ProvidedObjectPropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(ProvidedObjectPropertyReader.class);

	TargetReader targetReader;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;	
	
	@Override
	public void read(ProjectionStack stack, ExtractionContext context) throws ProjectionError {
	
		if (targetReader == null) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Read {}", targetReader.getType());
		}

		final Optional<Object> object = targetReader.read(stack, context);
		
		if (object.isPresent() ) {
			context.set(objectQualifier, object.get());
		}
	}
	
	public void setTargetReader(TargetReader targetReader) {
		this.targetReader = targetReader;
	}
	
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setObjectQualifier(String objectQualifier) {
		this.objectQualifier = objectQualifier;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}