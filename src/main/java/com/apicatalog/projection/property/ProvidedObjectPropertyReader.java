package com.apicatalog.projection.property;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.target.Extractor;
import com.apicatalog.projection.source.SourceType;

public class ProvidedObjectPropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(ProvidedObjectPropertyReader.class);

	Getter targetGetter;
	
	Set<Integer> visibleLevels;
	
	String objectQualifier;
	
	boolean optional;	
	
	Extractor extractor;
	
	@Override
	public void read(ProjectionStack stack, ExtractionContext context) throws ProjectionError {
		if (targetGetter == null) {
			return;
		}

		logger.debug("Read {} : {}, qualifier = {}, optional = {}, depth = {}", targetGetter.getName(), targetGetter.getType(), objectQualifier, optional, stack.length());

		Optional<Object> object = targetGetter.get(stack.peek());
		
		if (object.isEmpty()) {
			return;
		}

		if (extractor != null) {
			final Object value = object.get();
			
			Optional<ObjectType> sourceType = 
							Arrays.stream(context.getAcceptedTypes())
									.filter(type -> type.getType().isInstance(value))
									.findFirst()
									.map(type -> ObjectType.of(type.getType(), type.getComponentType()))
									;
			
			if (sourceType.isPresent()) {
				object = extractor.extract(sourceType.get(), value, context);
			}
		}
		
		if (object.isPresent() ) {
			context.set(objectQualifier, object.get());
		}
	}
	
	public void setTargetGetter(Getter getter) {
		this.targetGetter = getter;
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
	
	public void setExtractor(Extractor extractor) {
		this.extractor = extractor;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return Collections.emptySet();
	}

	@Override
	public String getName() {
		return targetGetter.getName();
	}
}