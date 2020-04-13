package com.apicatalog.projection.builder.writer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ConstantPropertyWriter;
import com.apicatalog.projection.property.target.TargetWriter;

public final class ConstantPropertyWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ConstantPropertyWriterBuilder.class);

	String[] constants;
	
	Setter setter;
	
	boolean reference;
	
	protected ConstantPropertyWriterBuilder() {
	}
	
	public static final ConstantPropertyWriterBuilder newInstance() {
		return new ConstantPropertyWriterBuilder();
	}
	
	public Optional<ConstantPropertyWriter> build(final ProjectionRegistry registry) {
		
		final ConstantPropertyWriter property = new ConstantPropertyWriter();
		
		Optional<TargetWriter> targetWriter = TargetWriterBuilder.newInstance()
										.setter(setter, reference)
										.build(registry);
 
		if (targetWriter.isEmpty()) {
			return Optional.empty();
		}

		// set constant values
		property.setConstants(constants);
		
		// set target writer
		property.setTargetWriter(targetWriter.get());
		
		// set source conversion if needed
		
		// TODO
		
		return Optional.of(property);
	}	
	
	public ConstantPropertyWriterBuilder targetSetter(final Setter targetSetter, final boolean reference) {
		this.setter = targetSetter;
		this.reference = reference;
		return this;
	}
	
	public ConstantPropertyWriterBuilder constants(final String[] constants) {
		this.constants = constants;
		return this;
	}
}
