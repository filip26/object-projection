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
	
	Class<?> objectClass;
	
	String qualifier;
	
	Boolean optional;

	public ProvidedMappingImpl(final ProjectionFactory index) {
		this.index = index;
	}
	
	@Override
	public Object compose(SourceObjects sources) throws ProjectionError, ConvertorError {
		
		if (objectClass.isAnnotationPresent(Projection.class)) {
			return index.compose(objectClass, sources.getValues());
		}

		final Optional<Object> source = 
				Optional.ofNullable(
					sources.get(objectClass, qualifier)
				);
			
		if (source.isEmpty()) {
			if (Boolean.TRUE.equals(optional)) {
				return null;
			}
			throw new ProjectionError("Source instance of " + objectClass.getCanonicalName() + ", qualifier=" + qualifier + ",  is not present.");
		}
		
		return source.get();
	}
	
	@Override
	public void decompose(Object object, SourceObjects sources) {
		// TODO Auto-generated method stub
		return;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
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
}
