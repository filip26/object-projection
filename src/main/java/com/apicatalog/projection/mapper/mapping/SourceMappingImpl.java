package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class SourceMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(SourceMappingImpl.class);

	final TypeAdapters adapters;
	
	Class<?> sourceClass;
	
	String propertyName;
	
	Class<?> sourcePropertyClass;
	Class<?> sourcePropertyComponentClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	String qualifier;
	
	Boolean optional;
	
	ConversionMapping[] conversions;

	public SourceMappingImpl(TypeAdapters adapters) {
		this.adapters = adapters;
	}
	
	@Override
	public Object compose(Path path, ContextObjects sources) throws ProjectionError {
		
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
	public void decompose(Path path, Object object, ContextObjects sources) throws ProjectionError {
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
			
			Object o = value.get();

			for (ConversionMapping conversion : revConversions) {
				o = conversion.backward(o);			
			}

			value = Optional.ofNullable(o);
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
		
		ObjectUtils.setPropertyValue(
						source.get(), 
						propertyName, 
						adapters.convert(sourcePropertyClass, sourcePropertyComponentClass, sourceValue)
						);	
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

	public Class<?> getPropertyClass() {
		return sourcePropertyClass;
	}

	public void setPropertyType(Class<?> propertyClass) {
		this.sourcePropertyClass = propertyClass;
	}
	
	public void setComponentClass(Class<?> componentClass) {
		this.sourcePropertyComponentClass = componentClass;
	}
	
	public Class<?> getComponentClass() {
		return sourcePropertyComponentClass;
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
