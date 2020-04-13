package com.apicatalog.projection.builder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.writer.TargetWriterBuilder;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.ProvidedObjectPropertyWriter;
import com.apicatalog.projection.property.target.TargetWriter;

public class ProvidedPropertyWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ProvidedPropertyWriterBuilder.class);

	Setter targetSetter;
	
	boolean targetReference;
	
	String qualifier;
	
	boolean optional;
	
	protected ProvidedPropertyWriterBuilder() {
		this.optional = false;
	}
	
	public static final ProvidedPropertyWriterBuilder newInstance() {
		return new ProvidedPropertyWriterBuilder();
	}
	
	public Optional<PropertyWriter> build(ProjectionRegistry registry) throws ProjectionError {

		if (targetSetter == null) {
			return Optional.empty();
		}

		TargetWriter targetWriter = TargetWriterBuilder.newInstance()
				.setter(targetSetter, targetReference)
				.build(registry)
				.orElseThrow(() -> new ProjectionError("Target is not readable"))
				;

		final ProvidedObjectPropertyWriter property = new ProvidedObjectPropertyWriter(registry);
		
		// set qualifier				
		property.setObjectQualifier(qualifier);
		
		property.setOptional(optional);
		
		property.setTargetWriter(targetWriter);
		
		return Optional.of(property);
	}
	

	public ProvidedPropertyWriterBuilder targetSetter(Setter setter) {
		this.targetSetter = setter;
		return this;
	}

	public ProvidedPropertyWriterBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}

	public ProvidedPropertyWriterBuilder qualifier(String qualifier) {
		this.qualifier = qualifier;
		return this;
	}

	public ProvidedPropertyWriterBuilder targetReference(boolean reference) {
		this.targetReference = reference;
		return this;
	}

}
