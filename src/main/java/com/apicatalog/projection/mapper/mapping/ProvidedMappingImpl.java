package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class ProvidedMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(ProvidedMappingImpl.class);
	
	final ProjectionFactory factory;
	final TypeAdapters typeAdapters;
	
	Class<?> sourceClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	String qualifier;
	
	Boolean optional;
	
	boolean reference;

	public ProvidedMappingImpl(final ProjectionFactory factory, final TypeAdapters typeAdapters) {
		this.factory = factory;
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object compose(Path path, ContextObjects contextObjects) throws ProjectionError {

		logger.debug("Compose path = {}, source = {}, qualifier = {}, optional = {}, reference = {}", path.length(), sourceClass.getSimpleName(), qualifier, optional, reference);

		if (reference) {	//FIXME ?!?!?!
			return factory.get(sourceClass).compose(path, contextObjects.getValues());				
		}

		final Optional<Object> source = 
				Optional.ofNullable(
					contextObjects.get(sourceClass, qualifier)
				);
			
		if (source.isEmpty()) {
			logger.trace("  providedValue = null");
			if (Boolean.TRUE.equals(optional)) {
				return null;
			}
			
			throw new ProjectionError("Source instance of " + sourceClass.getCanonicalName() + ", qualifier=" + qualifier + ",  is not present.");
		}
		
		final Object providedValue = source.get();
		
		logger.trace("  providedValue = {}", providedValue);
		
		return typeAdapters.convert(targetClass, targetComponentClass, providedValue);
	}
	
	@Override
	public void decompose(Path path, Object object, ContextObjects contextObjects) throws ProjectionError {

		logger.debug("Decompose {}, source = {}, qualifier = {}, optional = {}", object, sourceClass.getSimpleName(), qualifier, optional);

		Optional.ofNullable(object)
				.ifPresent(contextObjects::addOrReplace); //TODO deal with qualifier
		
	}

	public Class<?> getSourceClass() {
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
