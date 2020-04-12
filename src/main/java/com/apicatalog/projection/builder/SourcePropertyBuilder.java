package com.apicatalog.projection.builder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.source.SourceWriter;

public class SourcePropertyBuilder {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	SourceReader sourceReader;
	SourceWriter sourceWriter;
	
	AccessMode mode;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;

	protected SourcePropertyBuilder() {
		this.mode = AccessMode.READ_WRITE;
	}
	
	public static final SourcePropertyBuilder newInstance() {
		return new SourcePropertyBuilder();
	}
			
	public Optional<SourceProperty> build(ProjectionRegistry factory) {

		if (targetGetter == null && targetSetter == null) {
			return Optional.empty();
		}
		
		if (sourceReader == null && sourceWriter == null) {
			logger.warn(SOURCE_IS_MISSING, targetSetter != null ? targetSetter.getName() : targetGetter.getName());
			return Optional.empty();
		}

		final SourceProperty property = new SourceProperty();

		property.setSourceReader(sourceReader);
		property.setSourceWriter(sourceWriter);
		
		// set access mode
		switch (mode) {
		case READ_ONLY:
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setTargetSetter(targetSetter);			
			property.setTargetGetter(targetGetter);
			break;
		}			

		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(sourceReader != null ? sourceReader.getTargetType() : sourceWriter.getTargetType())		//FIXME split it reader/writer type
					.target(targetSetter != null ? targetSetter.getType() : targetGetter.getType(), targetReference) //TODO dtto
					.build(factory)
					);

		return Optional.of(property);		
	}

	public SourcePropertyBuilder sourceReader(SourceReader sourceReader) {
		this.sourceReader = sourceReader;
		return this;
	}

	public SourcePropertyBuilder targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}

	public SourcePropertyBuilder sourceWriter(SourceWriter sourceWriter) {
		this.sourceWriter = sourceWriter;
		return this;
	}

	public SourcePropertyBuilder targetGetter(Getter targetGetter) {
		this.targetGetter = targetGetter;
		return this;
	}

	public SourcePropertyBuilder mode(AccessMode mode) {
		this.mode = mode;
		return this;
	}	

	public SourcePropertyBuilder targetReference(boolean targetReference) {
		this.targetReference = targetReference;
		return this;
	}

}
