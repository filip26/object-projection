package com.apicatalog.projection.builder.reader;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.builder.ExtractorBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.ProvidedObjectPropertyReader;
import com.apicatalog.projection.property.ProvidedProjectionPropertyReader;

public final class ProvidedPropertyReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(ProvidedPropertyReaderBuilder.class);

	Getter targetGetter;
	
	String targetProjectionName;
	
	String qualifier;
	
	boolean optional;
	
	protected ProvidedPropertyReaderBuilder() {
		this.optional = false;
	}
	
	public static final ProvidedPropertyReaderBuilder newInstance() {
		return new ProvidedPropertyReaderBuilder();
	}
	
	public Optional<PropertyReader> build(Registry registry) {

		if (targetGetter == null) {
			return Optional.empty();
		}

		if (StringUtils.isNotBlank(targetProjectionName) && !targetGetter.getType().isCollection() && !targetGetter.getType().isArray()) {
			return buildReference(registry);
		}
		
		final ProvidedObjectPropertyReader property = ProvidedObjectPropertyReader.newInstance(targetGetter);

		// set qualifier				
		property.setObjectQualifier(qualifier);
		
		property.setOptional(optional);
		
		ExtractorBuilder
			.newInstance()
				.getter(targetGetter)
				.targetProjection(targetProjectionName)
				.build(registry)
			.ifPresent(property::setExtractor);
		
		return Optional.of(property);
	}
	

	Optional<PropertyReader> buildReference(Registry registry) {
		
		final ProvidedProjectionPropertyReader property = new ProvidedProjectionPropertyReader(targetProjectionName);

		property.setTargetGetter(targetGetter);

		// set qualifier
		property.setObjectQualifier(qualifier);

		property.setOptional(optional);

		registry.request(targetProjectionName, property::setProjection);
		
		return Optional.of(property);
	}
	
	public ProvidedPropertyReaderBuilder targetGetter(Getter getter) {
		this.targetGetter = getter;
		return this;
	}

	public ProvidedPropertyReaderBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}

	public ProvidedPropertyReaderBuilder qualifier(String qualifier) {
		this.qualifier = qualifier;
		return this;
	}

	public ProvidedPropertyReaderBuilder targetProjection(final String projectionName) {
		this.targetProjectionName = projectionName;
		return this;
	}
}
