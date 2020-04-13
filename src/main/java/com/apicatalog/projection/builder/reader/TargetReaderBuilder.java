package com.apicatalog.projection.builder.reader;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.target.ProjectionTargetReader;
import com.apicatalog.projection.property.target.ProjectionsTargetReader;
import com.apicatalog.projection.property.target.SimpleTargetReader;
import com.apicatalog.projection.property.target.TargetReader;

public class TargetReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(TargetReaderBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	Getter getter;
	
	boolean reference;
		
	protected TargetReaderBuilder() {
	}
	
	public static final TargetReaderBuilder newInstance() {
		return new TargetReaderBuilder();
	}
		
	public Optional<TargetReader> build(ProjectionRegistry registry) {
		
		if (getter == null) {
			return Optional.empty();
		}
		
		if (reference) {
			if (getter.getType().isCollection()) {
				return Optional.of(new ProjectionsTargetReader(getter));
			}
			
			return Optional.of(new ProjectionTargetReader(getter, registry));
		}
		

		return Optional.of(new SimpleTargetReader(getter));
	}
	
	public TargetReaderBuilder getter(Getter getter, boolean reference) {
		this.getter = getter;
		this.reference = reference;
		return this;
	}
}
