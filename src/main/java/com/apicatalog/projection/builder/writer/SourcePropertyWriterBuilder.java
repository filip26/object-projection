package com.apicatalog.projection.builder.writer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.property.SourcePropertyWriter;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.target.TargetWriter;

public final class SourcePropertyWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyWriterBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	SourceReader sourceReader;
	
	TargetWriter targetWriter;

	protected SourcePropertyWriterBuilder() {
	}
	
	public static final SourcePropertyWriterBuilder newInstance() {
		return new SourcePropertyWriterBuilder();
	}
			
	public Optional<SourcePropertyWriter> build(final ProjectionRegistry factory) {

		if (targetWriter == null && sourceReader == null) {
//TODO			logger.warn(SOURCE_IS_MISSING, targetSetter != null ? targetSetter.getName() : targetGetter.getName());
			return Optional.empty();
		}

		return Optional.of(new SourcePropertyWriter(sourceReader, targetWriter));		
	}

	public SourcePropertyWriterBuilder sourceReader(SourceReader sourceReader) {
		this.sourceReader = sourceReader;
		return this;
	}

	public SourcePropertyWriterBuilder targetWriter(TargetWriter writer) {
		this.targetWriter = writer;
		return this;
	}
}
