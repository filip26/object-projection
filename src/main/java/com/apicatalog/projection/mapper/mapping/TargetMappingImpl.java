package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.TargetMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class TargetMappingImpl implements TargetMapping {

	final Logger logger = LoggerFactory.getLogger(TargetMappingImpl.class);
	
	final ProjectionFactory factory;
	final TypeAdapters typeAdapters;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;

	Class<?> sourceClass;
	Class<?> sourceComponentClass;
	
	boolean reference;
	
	public TargetMappingImpl(ProjectionFactory index, TypeAdapters typeAdapters) {
		this.factory = index;
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object construct(final Path path, final Object object, final ContextObjects context) throws ProjectionError {
		
		logger.debug("Costruct target from {}, path = {}, colllection = {}, reference = {}", object, path, isCollection(), isReference());
		
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
				
				Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, targetComponentClass, object);
				
				final Collection<Object> collection = new ArrayList<>();

				// compose a projection from each object in the collection
				for (final Object item : sourceCollection) {
				
					final ContextObjects clonedSources = new ContextObjects(context);
					clonedSources.addOrReplace(item);
 
					collection.add(getReference(true).compose(path, clonedSources.getValues()));

				}
				
				value = Optional.of(collection);
				
			} else {				
				final ContextObjects clonedSources = new ContextObjects(context);
				value.ifPresent(clonedSources::addOrReplace);

				// compose a projection from a given value
				value = Optional.ofNullable(getReference(false).compose(path, clonedSources.getValues()));
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
	public Object deconstruct(final Path path, final Object object, final ContextObjects context) throws ProjectionError {

		logger.debug("Deconstruct {}, colllection = {}, reference = {}", object, isCollection(), isReference());
		
		if (Optional.ofNullable(object).isEmpty()) {
			return new Object[0];
		}
		
		Optional<Object> value = Optional.ofNullable(object);

		if (reference) {
			
			if (isCollection()) {
								
				final Collection<Object> collection = new ArrayList<>();

				Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, targetComponentClass, object);
				
				// extract objects from each projection in the collection
				for (final Object item : sourceCollection) {
					collection.add(filter(getReference(true).decompose(path, item), context));
				}
				
				value = Optional.of(collection);

			} else {
				value = Optional.ofNullable(filter(getReference(false).decompose(path, object), context));

			}
		}
		if (value.isEmpty()) {
			logger.trace("  sourceValue = null");
			return null;
		}

		final Object sourceValue = value.get();

		if (logger.isTraceEnabled()) {
			Stream.of(sourceValue).forEach(v -> logger.trace("  sourceValue = {}", v));
		}

		return sourceValue;
	}
	
	Object filter(Object[] objects, ContextObjects context) {
		if (objects == null) {
			return null;
		}
		
		if (objects.length == 1) {
			return objects[0];
		}
		
		Optional<Object> value = Optional.empty();
		
		for (Object object : objects) {
			
			if (value.isEmpty() 
					&& (sourceComponentClass != null ? sourceComponentClass.isInstance(object) : sourceClass.isInstance(object))
				) {
				
				value = Optional.ofNullable(object);
				
			} else {
				context.addOrReplace(object);
			}
		}
		return value.orElse(null);
	}
	
	@Override
	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public void setComponentClass(Class<?> itemClass) {
		this.targetComponentClass = itemClass;
	}
	
	@Override
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	@Override
	public boolean isCollection() {
		return targetComponentClass != null;
	}
	
	public boolean isReference() {
		return reference;
	}

	@SuppressWarnings("unchecked")
	public ProjectionMapping<Object> getReference(boolean collection) {
		return factory.get(collection ? (Class<Object>)targetComponentClass : (Class<Object>)targetClass);
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}
	
	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}
	
	public void setSourceComponentClass(Class<?> sourceComponentClass) {
		this.sourceComponentClass = sourceComponentClass;
	}
	
	@Override
	public Class<?> getSourceClass() {
		return sourceClass;
	}
	
	@Override
	public Class<?> getSourceComponentClass() {
		return sourceComponentClass;
	}
}
