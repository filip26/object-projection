package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.source.SourceType;

public class ProvidedProjectionPropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(ProvidedProjectionPropertyWriter.class);

	final String projectionName;
	
	Projection<?> projection;
	
	Setter targetSetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;
	
	public ProvidedProjectionPropertyWriter(final String projectionName) {
		this.projectionName = projectionName;
	}
	
	@Override
	public void write(ProjectionStack stack, CompositionContext context) throws ProjectionError {

		if (targetSetter == null) {
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Write {} : {}, qualifier = {}, optional = {}, depth = {}", targetSetter.getName(), targetSetter.getType(), objectQualifier, optional, stack.length());
		}

		if (projection == null) {
			throw new ProjectionError("Projection " + projectionName +  " is not set.");
		}
		
		final CompositionContext clonedContext = new CompositionContext(context);
		
		Optional.ofNullable(objectQualifier).ifPresent(clonedContext::namespace);
					
		final Object object = projection.getComposer().compose(stack, clonedContext);
		
		if (object != null) {
			targetSetter.set(stack.peek(), object);
		}
	}

	public void setTargetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
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

	@Override
	public Collection<SourceType> getSourceTypes() {
		return Collections.emptySet();
	}

	@Override
	public String getDependency() {
		return projectionName;
	}

	@Override
	public String getName() {
		return targetSetter.getName();
	}
	
	public void setProjection(Projection<?> projection) {
		this.projection = projection;
	}
}