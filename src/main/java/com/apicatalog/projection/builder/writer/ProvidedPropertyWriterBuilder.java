package com.apicatalog.projection.builder.writer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.ComposerBuilder;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.ProvidedObjectPropertyWriter;
import com.apicatalog.projection.property.ProvidedProjectionPropertyWriter;

public final class ProvidedPropertyWriterBuilder {

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
	
	public Optional<PropertyWriter> build(ProjectionRegistry registry) {

		if (targetSetter == null) {
			return Optional.empty();
		}

		if (targetReference && !targetSetter.getType().isCollection()) {
			return buildReference(registry);
		}

		final ProvidedObjectPropertyWriter property = new ProvidedObjectPropertyWriter(registry);
		
		// set qualifier				
		property.setObjectQualifier(qualifier);
		
		property.setOptional(optional);
		
		property.setTargetSetter(targetSetter);
		
		ComposerBuilder
			.newInstance()
				.setter(targetSetter, targetReference)
				.build(registry)
			.ifPresent(property::setComposer);
		
		return Optional.of(property);
	}
	

	Optional<PropertyWriter> buildReference(ProjectionRegistry registry) {
		
		final ProvidedProjectionPropertyWriter property = new ProvidedProjectionPropertyWriter(registry);

		property.setTargetSetter(targetSetter);
		
		// set qualifier
		property.setObjectQualifier(qualifier);

		property.setOptional(optional);

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
