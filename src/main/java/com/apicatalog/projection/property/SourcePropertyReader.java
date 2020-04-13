package com.apicatalog.projection.property;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.property.source.SourceWriter;
import com.apicatalog.projection.property.target.TargetReader;

public final class SourcePropertyReader implements PropertyReader {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyReader.class);

	final SourceWriter sourceWriter;
	
	final TargetReader targetReader;
	
	public SourcePropertyReader(final SourceWriter sourceWriter, final TargetReader targetReader) {
		this.sourceWriter = sourceWriter;
		this.targetReader = targetReader;
	}

	@Override
	public void read(final ProjectionStack queue, final ExtractionContext context) throws ProjectionError {

		if (sourceWriter == null || targetReader == null || !sourceWriter.isAnyTypeOf(context.getAcceptedTypes())) {
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Read {} from {}, depth = {}", sourceWriter.getType(), targetReader.getType(), queue.length());
		}

		final Optional<Object> object = targetReader.read(queue, context);

		if (object.isPresent()) {
			sourceWriter.write(queue, context, object.get());
		}
	}		
}
