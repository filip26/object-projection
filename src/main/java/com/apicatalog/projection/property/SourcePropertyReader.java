package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.source.SourceWriter;
import com.apicatalog.projection.property.target.TargetExtractor;
import com.apicatalog.projection.source.SourceType;

public final class SourcePropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyReader.class);

	final SourceWriter sourceWriter;
	
	final Getter targetGetter;
	
	TargetExtractor extractor;
	
	protected SourcePropertyReader(final SourceWriter sourceWriter, final Getter targetGetter) {
		this.sourceWriter = sourceWriter;
		this.targetGetter = targetGetter;
	}
	
	public static final SourcePropertyReader newInstance(final SourceWriter sourceWriter, final Getter targetGetter) {
		
		if (targetGetter == null) {
			throw new IllegalArgumentException("Target getter is not set.");
		}

		if (sourceWriter == null) {
			throw new IllegalArgumentException("Source writer is not set.");
		}
		
		return new SourcePropertyReader(sourceWriter, targetGetter);		
	}

	@Override
	public void read(final ProjectionStack stack, final ExtractionContext context) throws ExtractionError {

		if (!sourceWriter.isAnyTypeOf(context.getAcceptedTypes())) {
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Read {} from {}, depth = {}", sourceWriter.getTargetType(), targetGetter.getType(), stack.length());
		}

		try {
			Optional<Object> object = targetGetter.get(stack.peek());
			
			if (object.isEmpty()) {
				return;
			}
			
			if (extractor != null) {
				object = extractor.extract(sourceWriter.getTargetType(), object.get(), context);
			}
			
			if (object.isPresent()) {
				sourceWriter.write(context, object.get());
			}
			
		} catch (ObjectError e) {
			throw new ExtractionError("Can not get value of " + stack.peek().getClass().getCanonicalName() + "." + targetGetter.getName() + ".", e);
		}
	}

	public void setExtractor(TargetExtractor extractor) {
		this.extractor = extractor;
	}
	
	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceWriter.getSourceTypes();
	}
	
	@Override
	public String getDependency() {
		return extractor != null ? extractor.getProjectionName() : null;
	}

	@Override
	public String getName() {
		return targetGetter.getName();
	}		
}