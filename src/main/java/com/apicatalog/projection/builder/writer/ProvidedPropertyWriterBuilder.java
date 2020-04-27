package com.apicatalog.projection.builder.writer;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.builder.ComposerBuilder;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.ProvidedObjectPropertyWriter;
import com.apicatalog.projection.property.ProvidedProjectionPropertyWriter;

public final class ProvidedPropertyWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ProvidedPropertyWriterBuilder.class);

	Setter targetSetter;
	
	String targetProjectionName;
	
	String qualifier;
	
	boolean optional;
	
	protected ProvidedPropertyWriterBuilder() {
		this.optional = false;
	}
	
	public static final ProvidedPropertyWriterBuilder newInstance() {
		return new ProvidedPropertyWriterBuilder();
	}
	
	public Optional<PropertyWriter> build(Registry registry) {

		if (targetSetter == null) {
			return Optional.empty();
		}

		if (StringUtils.isNotBlank(targetProjectionName) && !targetSetter.getType().isCollection() && !targetSetter.getType().isArray()) {
			return buildReference(registry);
		}

		final ProvidedObjectPropertyWriter property = new ProvidedObjectPropertyWriter(registry);
		
		// set qualifier				
		property.setObjectQualifier(qualifier);
		
		property.setOptional(optional);
		
		property.setTargetSetter(targetSetter);
		
		ComposerBuilder
			.newInstance()
				.setter(targetSetter)
				.targetProjection(targetProjectionName)
				.build(registry)
			.ifPresent(property::setComposer);
		
		return Optional.of(property);
	}
	

	Optional<PropertyWriter> buildReference(Registry registry) {

		final ProvidedProjectionPropertyWriter property = new ProvidedProjectionPropertyWriter(targetProjectionName);

		property.setTargetSetter(targetSetter);
		

		// set qualifier
		property.setObjectQualifier(qualifier);

		property.setOptional(optional);

		registry.request(targetProjectionName, property::setProjection);
		
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

	public ProvidedPropertyWriterBuilder targetProjection(final String projectionName) {
		this.targetProjectionName = projectionName;
		return this;
	}
}
