package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.source.SourceWriter;
import com.apicatalog.projection.property.target.Extractor;
import com.apicatalog.projection.source.SourceType;

public final class SourcePropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyReader.class);

	final SourceWriter sourceWriter;
	
	final Getter targetGetter;
	
	final Extractor extractor;
	
	public SourcePropertyReader(final SourceWriter sourceWriter, final Getter targetGetter, final Extractor extractor) {
		this.sourceWriter = sourceWriter;
		this.targetGetter = targetGetter;
		this.extractor = extractor;
	}

	@Override
	public void read(final ProjectionStack stack, final ExtractionContext context) throws ProjectionError {

		if (sourceWriter == null || targetGetter == null || !sourceWriter.isAnyTypeOf(context.getAcceptedTypes())) {
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Read {} from {}, depth = {}", sourceWriter.getTargetType(), targetGetter.getType(), stack.length());
		}

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
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceWriter.getSourceTypes();
	}
	
	@Override
	public Collection<String> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public String getName() {
		return targetGetter.getName();
	}		
}