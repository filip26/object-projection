package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class ProvidedMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(ProvidedMappingImpl.class);
	
	final ProjectionFactory factory;
	
	Class<?> sourceClass;
	Class<?> sourceComponentClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	String qualifier;
	
	Boolean optional;
	
	boolean reference;

	public ProvidedMappingImpl(final ProjectionFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public Object compose(Path path, ContextObjects context) throws ProjectionError {
		
		if (reference) {	//FIXME ?!?!?!
			return factory.get(sourceClass).compose(path, context.getValues());				
		}

		final Optional<Object> source = 
				Optional.ofNullable(
					context.get(sourceClass, qualifier)
				);
			
		if (source.isEmpty()) {
			
			if (Boolean.TRUE.equals(optional)) {
				return null;
			}
			
			throw new ProjectionError("Source instance of " + sourceClass.getCanonicalName() + ", qualifier=" + qualifier + ",  is not present.");
		}
		
		return source.get();
	}
	
	@Override
	public void decompose(Path path, Object object, ContextObjects sources) throws ProjectionError {

		logger.debug("Decompose {}, source={}, qualifier={}, optional={}", object, sourceClass.getSimpleName(), qualifier, optional);

		Optional.ofNullable(object)
				.ifPresent(v -> sources.addOrReplace(v)); //TODO deal with qualifier
		
	}

	public Class<?> getObjectClass() {
		return sourceClass;
	}

	public void setSourceClass(Class<?> objectClass) {
		this.sourceClass = objectClass;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public boolean getReference() {
		return reference;
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}
	
	@Override
	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	@Override
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
}
