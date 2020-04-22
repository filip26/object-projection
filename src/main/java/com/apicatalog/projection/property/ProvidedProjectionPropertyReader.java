package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.source.SourceType;

public class ProvidedProjectionPropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(ProvidedProjectionPropertyReader.class);

	final String projectionName;
	
	ProjectionExtractor<Object> extractor;
	
	Getter targetGetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;
	
	public ProvidedProjectionPropertyReader(final String projectionName) {
		this.projectionName = projectionName;
	}
	
	@Override
	public void read(ProjectionStack stack, ExtractionContext context) throws CompositionError {

		if (targetGetter == null) {
			return;
		}

		logger.debug("Read {} : {}, qualifier = {}, optional = {}, depth = {}", targetGetter.getName(), targetGetter.getType(), objectQualifier, optional, stack.length());

		if (extractor == null) {
			throw new CompositionError("Projection " + targetGetter.getType().getType() +  " is not set.");			
		}

		try {
			final Optional<Object> object = targetGetter.get(stack.peek());
	
			if (object.isPresent()) {
				
				Optional.ofNullable(objectQualifier).ifPresent(context::addNamespace);
	
				extractor.extract(object.get(), context);
				
				Optional.ofNullable(objectQualifier).ifPresent(s -> context.removeLastNamespace());
			}
		} catch (ObjectError e) {
			throw new CompositionError("Can not get value of " + stack.peek().getClass().getCanonicalName() + "." + targetGetter.getName() + ".");
		}
	}
	
	public void setTargetGetter(Getter targetGetter) {
		this.targetGetter = targetGetter;
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
		return targetGetter.getName();
	}
	
	@SuppressWarnings("unchecked")
	public void setProjection(final Projection<?> projection) {
		this.extractor = (ProjectionExtractor<Object>) projection.getExtractor().orElse(null);
	}
}