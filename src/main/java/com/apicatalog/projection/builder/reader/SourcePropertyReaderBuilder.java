package com.apicatalog.projection.builder.reader;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.Builder;
import com.apicatalog.projection.builder.ExtractorBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.SourcePropertyReader;
import com.apicatalog.projection.property.source.SourceWriter;

public final class SourcePropertyReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyReaderBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	Builder<SourceWriter> sourceWriterBuilder;
	
	Getter targetGetter;
	
	String targetProjectionName;
	
	protected SourcePropertyReaderBuilder() {
	}
	
	public static final SourcePropertyReaderBuilder newInstance() {
		return new SourcePropertyReaderBuilder();
	}
			
	public Optional<SourcePropertyReader> build(final Registry registry) throws ProjectionError {

		if (targetGetter == null) {
			logger.warn("Target getter is missing. Skipping source.");
			return Optional.empty();
		}

		if (sourceWriterBuilder == null) {
			logger.warn(SOURCE_IS_MISSING, targetGetter.getName());
			return Optional.empty();
		}

		final Optional<SourceWriter> sourceWriter = sourceWriterBuilder
							.targetType(targetGetter.getType()).targetProjection(targetProjectionName)
							.build(registry.getTypeConversions());
		
		if (sourceWriter.isEmpty()) {
			logger.warn(SOURCE_IS_MISSING, targetGetter.getName());
			return Optional.empty();			
		}
		
		final SourcePropertyReader sourcePropertyReader = SourcePropertyReader.newInstance(sourceWriter.get(), targetGetter);
			  
		ExtractorBuilder.newInstance()
			.getter(targetGetter)
			.targetProjection(targetProjectionName)
			.build(registry)
			.ifPresent(e -> {
					sourcePropertyReader.setExtractor(e);
					sourceWriterBuilder.targetType(targetGetter.getType()).targetProjection(targetProjectionName);
				});		

		return Optional.of(sourcePropertyReader);
	}

	public SourcePropertyReaderBuilder sourceWriter(Builder<SourceWriter> sourceWriterBuilder) {
		this.sourceWriterBuilder = sourceWriterBuilder;
		return this;
	}

	public SourcePropertyReaderBuilder target(final Getter getter) {
		this.targetGetter = getter;
		return this;
	}
	
	public SourcePropertyReaderBuilder targetProjection(final String projectionName) {
		this.targetProjectionName = projectionName;
		return this;
	}

}
