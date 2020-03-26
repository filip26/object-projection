package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class ProvidedMappingImpl implements SourceMapping {

	final ProjectionFactory index;
	
	Class<?> sourceClass;
	
	String qualifier;
	
	Boolean optional;

	public ProvidedMappingImpl(final ProjectionFactory index) {
		this.index = index;
	}
	
	@Override
	public Object compose(SourceObjects sources) throws ProjectionError, ConvertorError {
		
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
	public void decompose(Object[] object, SourceObjects sources) {
		// TODO Auto-generated method stub
		return;
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
