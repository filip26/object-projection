package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class ProvidedMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(ProvidedMappingImpl.class);
	
	final ProjectionFactory factory;
	final TypeAdapters typeAdapters;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	String qualifier;
	
	Boolean optional;
	
	boolean reference;
	
	AccessMode accessMode;

	public ProvidedMappingImpl(final ProjectionFactory factory, final TypeAdapters typeAdapters) {
		this.factory = factory;
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object compose(Path path, ContextObjects contextObjects) throws ProjectionError {

		logger.debug("Compose path = {}, target = {}, qualifier = {}, optional = {}, reference = {}", path.length(), targetClass.getSimpleName(), qualifier, optional, reference);

		if (reference) {
			return factory.get(targetClass).compose(path, contextObjects.getValues());
			//TODO deal with a collection
			
		}

		final Optional<Object> source = 
				Optional.ofNullable(
					contextObjects.get(targetClass, qualifier)
				);
			
		if (source.isEmpty()) {
			logger.trace("  providedValue = null");
			if (Boolean.TRUE.equals(optional)) {
				return null;
			}
			
			throw new ProjectionError("Source instance of " + targetClass.getCanonicalName() + ", qualifier=" + qualifier + ",  is not present.");
		}
		
		final Object providedValue = source.get();
		
		logger.trace("  providedValue = {}", providedValue);
		return providedValue;
	}
	
	@Override
	public void decompose(Object object, ContextObjects contextObjects) throws ProjectionError {

		logger.debug("Decompose {}, source = {}, qualifier = {}, optional = {}", object, targetClass.getSimpleName(), qualifier, optional);

		Optional.ofNullable(object)
				.ifPresent(v -> contextObjects.addOrReplace(v, qualifier));
		
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
	
	@Override
	public AccessMode getAccessMode() {
		return accessMode;
	}
	
	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}
}
