package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class SourceMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(SourceMappingImpl.class);

	final TypeAdapters adapters;
	
	Class<?> sourceClass;
	
	String propertyName;
	
	Class<?> propertyClass;
	
	String qualifier;
	
	Boolean optional;
	
	ConversionMapping[] conversions;

	public SourceMappingImpl(TypeAdapters adapters) {
		this.adapters = adapters;
	}
	
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
	public void decompose(Object[] objects, SourceObjects sources) throws ConvertorError, ProjectionError {
		logger.debug("Decompose {}, source={}, qualifier={}, optional={}", objects, sourceClass.getSimpleName(), qualifier, optional);

		Optional<Object> value = Optional.empty(); 
		
		if (objects.length == 1) {
			value = Optional.ofNullable(objects[0]);
			
		} else {
			for (Object o : objects) {
				if (propertyClass.isInstance(o)) {
					value = Optional.ofNullable(o);
				} else {
					sources.addOrReplace(o);
				}
			}
		}
		
		if (value.isEmpty()) {
			return;
		}
		
		//TODO hack, filter collection
		if (Collection.class.isInstance(value.get())) {
			
			ArrayList<Object> l = new ArrayList<>();
			
			for (Object[] o : (Collection<Object[]>)value.get()) {

				if (o.length == 1) {
					l.add(o[0]);
					
				} else {
					for (Object o1 : o) {
						if (propertyClass.isInstance(o1)) {
							l.add(o1);
						} else {
							sources.addOrReplace(o1);
						}
					}
				}

			}
			
			
			value = Optional.of(l);
			
		}
		
		// apply explicit deconversions
		if (Optional.ofNullable(conversions).isPresent()) {
			
			// reverse order
			final ArrayList<ConversionMapping> revConversions = new ArrayList<>(Arrays.asList(conversions));
			Collections.reverse(revConversions);
			
			Object o = value.get();
			
			for (ConversionMapping conversion : revConversions) {
				
				o = conversion.backward(o);
				if (o != null && Object[].class.isInstance(o)) {
					o = ((Object[])o)[0];	//FIXME hack
				}
				if (value.isEmpty()) {
					break;
				}				
			}
			value = Optional.ofNullable(o);
		}

		if (value.isEmpty()) {
			logger.trace("  = null");
			return;
		}
		
		Object sourceValue = value.get();
				
		logger.trace("  = {}", sourceValue);
		
		Optional<Object> source = 
				Optional.ofNullable(
					sources.get(sourceClass, qualifier)
				);
		
		if (source.isEmpty()) {
			source = Optional.of(ObjectUtils.newInstance(sourceClass));
			sources.addOrReplace(source.get());	 //TODO deal with qualifier
		}
		
		ObjectUtils.setPropertyValue(source.get(), propertyName, adapters.convert(propertyClass, sourceValue));	
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
		return propertyClass;
	}

	public void setPropertyType(Class<?> propertyClass) {
		this.propertyClass = propertyClass;
	}
}
