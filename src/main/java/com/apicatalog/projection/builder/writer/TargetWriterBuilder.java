package com.apicatalog.projection.builder.writer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.target.ProjectionTargetWriter;
import com.apicatalog.projection.property.target.ProjectionsTargetWriter;
import com.apicatalog.projection.property.target.SimpleTargetWriter;
import com.apicatalog.projection.property.target.TargetWriter;

public class TargetWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(TargetWriterBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 

	Setter setter;

	boolean reference;
		
	protected TargetWriterBuilder() {
	}
	
	public static final TargetWriterBuilder newInstance() {
		return new TargetWriterBuilder();
	}
		
	public Optional<TargetWriter> build(ProjectionRegistry registry) {

		if (setter == null) {
			return Optional.empty();
		}
		
		if (reference) {
			if (setter.getType().isCollection()) {
				return Optional.of(new ProjectionsTargetWriter(setter));
			}
			
			return Optional.of(new ProjectionTargetWriter(registry, setter));
		}
		

		return Optional.of(new SimpleTargetWriter(setter));

	}

	
	
	public TargetWriterBuilder setter(Setter setter, boolean reference) {
		this.setter = setter;
		this.reference = reference;
		return this;
	}
}
