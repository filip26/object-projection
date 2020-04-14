package com.apicatalog.projection.builder.writer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.SourcePropertyWriter;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.target.Composer;

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
			
	public Optional<SourcePropertyWriter> build(final ProjectionRegistry registry) {

		if (targetSetter == null && sourceReader == null) {
//TODO			logger.warn(SOURCE_IS_MISSING, targetSetter != null ? targetSetter.getName() : targetGetter.getName());
			return Optional.empty();
		}
		
		final Optional<Composer> composer =  
				ComposerBuilder.newInstance()
					.setter(targetSetter, targetReference)
					.build(registry);

		return Optional.of(new SourcePropertyWriter(sourceReader, targetSetter, composer.orElse(null)));		
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
