package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class SourceMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(SourceMappingImpl.class);

	Class<?> sourceClass;
	
	String propertyName;
	
	String qualifier;
	
	Boolean optional;
	
	ConversionMapping[] conversions;

	@Override
	public Object compose(SourceObjects sources) throws ProjectionError, ConvertorError {
		
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
			
		Object value = ObjectUtils.getPropertyValue(source.get(), propertyName);

		// apply explicit conversions
		if (conversions != null) {
			for (ConversionMapping conversion : conversions) {
				value = conversion.forward(value);
			}
		}
		
		return value;
	}

	@Override
	public void decompose(Object object, SourceObjects sources) throws ConvertorError, ProjectionError {
		logger.debug("Decompose {}, source={}, qualifier={}, optional={}", object, sourceClass.getSimpleName(), qualifier, optional);

		Optional<Object> value = Optional.ofNullable(object);
		
		if (value.isEmpty()) {
			return;
		}
		
		// apply explicit deconversions
		if (Optional.ofNullable(conversions).isPresent()) {
			
			// reverse order
			final ArrayList<ConversionMapping> revConversions = new ArrayList<>(Arrays.asList(conversions));
			Collections.reverse(revConversions);
			
			for (ConversionMapping conversion : revConversions) {
				
				value = Optional.ofNullable(conversion.backward(value.get()));
				
				if (value.isEmpty()) {
					break;
				}
			}
		}

		if (value.isEmpty()) {
			logger.trace("  = null");
			return;
		}
		
		final Object sourceValue = value.get();
		
		logger.trace("  = {}", sourceValue);
		
		Optional<Object> source = 
				Optional.ofNullable(
					sources.get(sourceClass, qualifier)
				);
		
		if (source.isEmpty()) {
			source = Optional.of(ObjectUtils.newInstance(sourceClass));
			sources.addOrReplace(source.get());	 //TODO deal with qualifier
		}
		
		ObjectUtils.setPropertyValue(source.get(), propertyName, sourceValue);	
	}
	
	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public 	ConversionMapping[] getConversions() {
		return conversions;
	}

	public void setConversions(ConversionMapping[] conversions) {
		this.conversions = conversions;
	}

	public boolean isOptional() {
		return optional != null && optional;
	}
	
	public void setOptional(Boolean optional) {
		this.optional = optional;
	}
}
