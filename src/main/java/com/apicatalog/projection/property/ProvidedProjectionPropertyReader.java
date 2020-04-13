package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.getter.Getter;

public class ProvidedProjectionPropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(ProvidedProjectionPropertyReader.class);

	final ProjectionRegistry factory;
	
	Getter targetGetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;
	
	public ProvidedProjectionPropertyReader(ProjectionRegistry factory) {
		this.factory = factory;
	}
	
	@Override
	public void read(ProjectionStack stack, ExtractionContext context) throws ProjectionError {

		if (targetGetter == null) {
			return;
		}

		logger.debug("Backward {} : {}, qualifier = {}, optional = {}, depth = {}", targetGetter.getName(), targetGetter.getType(), objectQualifier, optional, stack.length());

		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetGetter.getType().getType()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetGetter.getType().getType() +  " is not present.");			
		}

		Optional.ofNullable(objectQualifier).ifPresent(context::addNamespace);

		final Optional<Object> object = targetGetter.get(stack.peek());

		if (object.isPresent()) {
			projection.extract(object.get(), context);
		}

		Optional.ofNullable(objectQualifier).ifPresent(s -> context.removeLastNamespace());
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
}