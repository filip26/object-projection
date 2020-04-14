package com.apicatalog.projection.builder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.ProvidedObjectPropertyReader;
import com.apicatalog.projection.property.ProvidedProjectionPropertyReader;

public class ProvidedPropertyReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(ProvidedPropertyReaderBuilder.class);

	Getter targetGetter;
	
	boolean targetReference;
	
	String qualifier;
	
	boolean optional;
	
	protected ProvidedPropertyReaderBuilder() {
		this.optional = false;
	}
	
	public static final ProvidedPropertyReaderBuilder newInstance() {
		return new ProvidedPropertyReaderBuilder();
	}
	
	public Optional<PropertyReader> build(ProjectionRegistry registry) throws ProjectionError {

		if (targetGetter == null) {
			return Optional.empty();
		}

		if (targetReference && !targetGetter.getType().isCollection()) {
			return buildReference(registry);
		}
		
//		TargetReader targetReader = ExtractorBuilder.newInstance()
//										.getter(targetGetter, targetReference)
//										.build(registry)
//										.orElseThrow(() -> new ProjectionError("Target is not readable"))
//										;
		
		final ProvidedObjectPropertyReader property = new ProvidedObjectPropertyReader();

		// set qualifier				
		property.setObjectQualifier(qualifier);
		
		property.setOptional(optional);
		property.setTargetGetter(targetGetter);
		
		return Optional.of(property);
	}
	

	Optional<PropertyReader> buildReference(ProjectionRegistry registry) {
		
		final ProvidedProjectionPropertyReader property = new ProvidedProjectionPropertyReader(registry);

		property.setTargetGetter(targetGetter);

		// set qualifier
		property.setObjectQualifier(qualifier);

		property.setOptional(optional);

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

	public ProvidedPropertyReaderBuilder targetReference(boolean reference) {
		this.targetReference = reference;
		return this;
	}

}
