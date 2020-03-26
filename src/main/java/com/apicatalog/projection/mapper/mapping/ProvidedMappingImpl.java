package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class ProvidedMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(PropertyMappingImpl.class);
	
	final ProjectionFactory index;
	
	Class<?> sourceClass;
	
	String qualifier;
	
	Boolean optional;

	public ProvidedMappingImpl(final ProjectionFactory index) {
		this.index = index;
	}
	
	@Override
	public Object compose(SourceObjects sources) throws ProjectionError, ConverterError {
		
		if (sourceClass.isAnnotationPresent(Projection.class)) {
			return index.compose(sourceClass, sources.getValues());
		}

		final Optional<Object> source = 
				Optional.ofNullable(
					sources.get(sourceClass, qualifier)
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
	public void decompose(Object[] objects, SourceObjects sources) throws ProjectionError {
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
}
