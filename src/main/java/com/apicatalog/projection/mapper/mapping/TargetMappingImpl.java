package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.TargetMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class TargetMappingImpl implements TargetMapping {

	final Logger logger = LoggerFactory.getLogger(TargetMappingImpl.class);
	
	final ProjectionFactory index;
	
	Class<?> targetClass;
	Class<?> itemClass;

	boolean reference;
	
	public TargetMappingImpl(ProjectionFactory index) {
		this.index = index;
	}
	
	@Override
	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<? extends Object> targetClass) {
		this.targetClass = targetClass;
	}

	public void setItemClass(Class<?> itemClass) {
		this.itemClass = itemClass;
	}
	
	@Override
	public Class<?> getItemClass() {
		return itemClass;
	}
	
	@Override
	public boolean isCollection() {
		return itemClass != null;
	}
	
	public boolean isReference() {
		return reference;
	}

	@SuppressWarnings("unchecked")
	public ProjectionMapping<Object> getReference(boolean collection) {
		return index.get(collection ? (Class<Object>)itemClass : (Class<Object>)targetClass);
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}

	@Override
	public Object construct(final int level, final Object object, final SourceObjects sources) throws ProjectionError, ConvertorError {
		
		logger.debug("Costruct target from {} at level {}, colllection={}, reference={}", object, level, isCollection(), isReference());
		
		Optional<Object> value = Optional.ofNullable(object);

		if (value.isEmpty()) {
			logger.trace("  value = null");
			return null;
		}
		
		// is the target a reference on a projection
		if (reference) {

			// is the target a collection of references?
			if (isCollection()) {
				Object untyppedCollection = value.get();
				
				logger.trace("  collection={}", untyppedCollection.getClass().getCanonicalName());
				
				final Collection<Object> collection = new ArrayList<>();

				// compose a projection from each object in the collection
				for (final Object item : (Collection<Object>)untyppedCollection) { //FIXME check value type, do implicit collection conversion if needed	

					final SourceObjects clonedSources = new SourceObjects(sources);
					clonedSources.addOrReplace(item);
 
					collection.add(getReference(true).compose(clonedSources.getValues()));	//FIXME level + 1,

				}
				
				value = Optional.of(collection);
				
			} else {				
				final SourceObjects clonedSources = new SourceObjects(sources);
				value.ifPresent(clonedSources::addOrReplace);

				// compose a projection from a given value
				value = Optional.ofNullable(getReference(false).compose(clonedSources.getValues()));	//FIXME level + 1, 
			}
		}

		if (value.isEmpty()) {
			logger.trace("  value = null");
			return null;
		}

		//FIXME do implicit conversion if needed

		Object targetValue = value.get();
		logger.trace("  value = {}", targetValue);

		return targetValue;
	}

	@Override
	public Object deconstruct(Object object, SourceObjects sources) throws ProjectionError, ConvertorError {

		logger.debug("Deconstruct source from {}, colllection={}, reference={}", object, isCollection(), isReference());
		
		Optional<Object> value = Optional.ofNullable(object);

		if (value.isEmpty()) {
			return null;
		}
		
		if (reference) {
			
			if (isCollection()) {

				Object untyppedCollection = value.get();
				
				logger.trace("  collection={}", untyppedCollection.getClass().getCanonicalName());
				
				final Collection<Object> collection = new ArrayList<>();

				// compose a projection from each object in the collection
				for (final Object item : (Collection<Object>)untyppedCollection) { //FIXME check value type, do implicit collection conversion if needed	 
					collection.add(extract(getReference(true).decompose(item), sources, false));
				}
				
				value = Optional.of(collection);


			} else {
				value = Optional.ofNullable(extract(getReference(false).decompose(value.get()), sources, false));
			}
		}

		if (value.isEmpty()) {
			logger.trace("  value = null");
			return null;
		}

		Object sourceValue = value.get();
		logger.trace("  value = {}", sourceValue);

		return sourceValue;
	}
	
	Object extract(Object[] objects, SourceObjects sources, boolean collection) {
		
		Optional<Object> object = Optional.empty();
		
		for (Object o : objects) {
//			System.out.println(">>> " + o);
			if (targetClass.isInstance(o)) {
				object = Optional.of(o);
			} else {
				sources.addOrReplace(o);
			}
			
		}
		return object.isPresent() ? object.get() : null;		
	}
	
}
