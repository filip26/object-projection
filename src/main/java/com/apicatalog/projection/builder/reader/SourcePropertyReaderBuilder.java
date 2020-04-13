package com.apicatalog.projection.builder.reader;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.property.SourcePropertyReader;
import com.apicatalog.projection.property.source.SourceWriter;
import com.apicatalog.projection.property.target.TargetReader;

public class SourcePropertyReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyReaderBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	SourceWriter sourceWriter;
	
	TargetReader targetReader;
	
	protected SourcePropertyReaderBuilder() {
	}
	
	public static final SourcePropertyReaderBuilder newInstance() {
		return new SourcePropertyReaderBuilder();
	}
			
	public Optional<SourcePropertyReader> build(final ProjectionRegistry registry) {

		if (targetReader == null && sourceWriter == null) {
//TODO			logger.warn(SOURCE_IS_MISSING, targetSetter != null ? targetSetter.getName() : targetGetter.getName());
			return Optional.empty();
		}
		
		return Optional.of(new SourcePropertyReader(sourceWriter, targetReader));
	}

	public SourcePropertyReaderBuilder sourceWriter(SourceWriter sourceWriter) {
		this.sourceWriter = sourceWriter;
		return this;
	}

	public SourcePropertyReaderBuilder targetReader(TargetReader targetReader) {
		this.targetReader = targetReader;
		return this;
	}
}
