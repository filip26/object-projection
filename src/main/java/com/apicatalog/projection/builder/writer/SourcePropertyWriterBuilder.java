package com.apicatalog.projection.builder.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
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
	
	boolean targetReference;

	protected SourcePropertyWriterBuilder() {
	}
	
	public static final SourcePropertyWriterBuilder newInstance() {
		return new SourcePropertyWriterBuilder();
	}
			
	public SourcePropertyWriter build(final ProjectionRegistry registry) throws ProjectionError {

		if (targetSetter == null) {
			throw new ProjectionError("Target setter is missing.");

		}

		if (sourceReader == null) {
			throw new ProjectionError("Source reader is not set.");
		}		
		
		final SourcePropertyWriter sourcePropertyWriter = SourcePropertyWriter.newInstance(sourceReader, targetSetter);
		  
		ComposerBuilder.newInstance()
			.setter(targetSetter, targetReference)
			.build(registry)
			.ifPresent(sourcePropertyWriter::setComposer);

		return sourcePropertyWriter;		
	}

	public SourcePropertyWriterBuilder sourceReader(SourceReader sourceReader) {
		this.sourceReader = sourceReader;
		return this;
	}

	public SourcePropertyWriterBuilder target(Setter setter, boolean reference) {
		this.targetSetter = setter;
		this.targetReference = reference;
		return this;
	}
}
