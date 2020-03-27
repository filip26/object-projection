package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ContextObjects;

public class ProvidedMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(ProvidedMappingImpl.class);
	
	final ProjectionFactory factory;
	
	Class<?> sourceClass;
	
	String qualifier;
	
	Boolean optional;
	
	boolean reference;

	public ProvidedMappingImpl(final ProjectionFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public Object compose(ContextObjects context) throws ProjectionError {
		
		if (reference) {
			return factory.compose(sourceClass, context.getValues());				
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
	public void decompose(Object[] objects, ContextObjects sources) throws ProjectionError {

		logger.debug("Decompose {}, source={}, qualifier={}, optional={}", objects, sourceClass.getSimpleName(), qualifier, optional);

		Optional<Object> value = Optional.empty(); 
		
		value = Optional.ofNullable(objects[0]);	//FIXME ?!
					
		if (value.isEmpty()) {
			return;
		}
				
		Object sourceValue = value.get();
				
		logger.trace("  = {}", sourceValue);
		
		
		sources.addOrReplace(value.get());	 //TODO deal with qualifier
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
}
