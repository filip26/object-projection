package com.apicatalog.projection.builder.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.ComposerBuilder;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.SourcePropertyWriter;
import com.apicatalog.projection.property.source.SourceReader;

public final class SourcePropertyWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyWriterBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	SourceReader sourceReader;
	
	Setter targetSetter;
	
	String targetProjectionName;

	protected SourcePropertyWriterBuilder() {
	}
	
	public static final SourcePropertyWriterBuilder newInstance() {
		return new SourcePropertyWriterBuilder();
	}
			
	public SourcePropertyWriter build(final Registry registry) throws ProjectionError {

		if (targetSetter == null) {
			throw new ProjectionError("Target setter is missing.");

		}

		if (sourceReader == null) {
			throw new ProjectionError("Source reader is not set.");
		}		
		
		final SourcePropertyWriter sourcePropertyWriter = SourcePropertyWriter.newInstance(sourceReader, targetSetter);
		  
		ComposerBuilder.newInstance()
			.setter(targetSetter)
			.targetProjection(targetProjectionName)
			.build(registry)
			.ifPresent(sourcePropertyWriter::setComposer);

		return sourcePropertyWriter;		
	}

	public SourcePropertyWriterBuilder sourceReader(final SourceReader sourceReader) {
		this.sourceReader = sourceReader;
		return this;
	}

	public SourcePropertyWriterBuilder target(final Setter setter) {
		this.targetSetter = setter;
		return this;
	}
	
	public SourcePropertyWriterBuilder targetProjection(final String projectionName) {
		this.targetProjectionName = projectionName;
		return this;
	}
}
