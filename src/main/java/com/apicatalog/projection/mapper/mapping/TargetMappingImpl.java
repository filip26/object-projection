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

	Class<?> sourceClass;
	Class<?> sourceComponentClass;

	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	boolean reference;
	
	public TargetMappingImpl(ProjectionFactory factory, TypeAdapters typeAdapters) {
		this.factory = factory;
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object construct(final Path path, final Object object, final ContextObjects contextObjects) throws ProjectionError {
		
		logger.debug("Costruct target from {}, path = {}, colllection = {}, reference = {}", object, path.length(), isCollection(), isReference());
		
		Optional<Object> value = Optional.ofNullable(object);
		if (value.isEmpty()) {
			logger.trace("  value = null");
			return null;
		}
		
		// is the target a reference on a projection
		if (reference) {

			// is the target a collection of references?
			if (isCollection()) {

				
				final Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, Object.class, object);
				
				final Collection<Object> collection = new ArrayList<>();
				
				// compose a projection from each object in the collection
				for (final Object item : sourceCollection) {				
					collection.add(getTargetType(true)
									.compose(
										path,										
										(new ContextObjects(contextObjects)).addOrReplace(item, null).getValues()
										)
									);
				}
				
				value = Optional.of(collection);
				
			} else {				
				final ContextObjects clonedSources = new ContextObjects(contextObjects);
				value.ifPresent(v -> clonedSources.addOrReplace(v, null));
				
				final ProjectionMapping<Object> projection = getTargetType(false);
				
				if (projection != null) {
					value = Optional.ofNullable(projection.compose(path, clonedSources.getValues()));
				} else {
					value = Optional.empty();
				}
			}
		}

		if (value.isEmpty()) {
			logger.trace("  value = null");
			return null;
		}

		final Object targetValue = typeAdapters.convert(
										targetClass,
										targetComponentClass,
										value.get()
										);

		
		logger.trace("  value = {}", targetValue);

		return targetValue;
	}

	@Override
	public Object deconstruct(final Object object, final ContextObjects context) throws ProjectionError {

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
					collection.add(filter(getTargetType(true).decompose(item, context), context));
				}
				
				value = Optional.of(collection);

			} else {
				
				final ProjectionMapping<Object> projection = getTargetType(false);
				
				if (projection != null) {
					value = Optional.ofNullable(filter(projection.decompose(object, context), context));
				} else {
					value = Optional.empty();
				}
				

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
		
		//FIXME return typeAdapters.convert(sourceClass, sourceComponentClass, sourceValue);
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
				if (!context.contains(object.getClass(), null)) {
					context.addOrReplace(object, null);
				}
				
				//context.merge(object, null);
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

	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
	
	@Override
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public boolean isCollection() {
		return targetComponentClass != null;
	}
	
	public boolean isReference() {
		return reference;
	}

	@SuppressWarnings("unchecked")
	ProjectionMapping<Object> getTargetType(boolean collection) throws ProjectionError {
		ProjectionMapping<Object> mapping = factory.get(collection ? (Class<Object>)targetComponentClass : (Class<Object>)targetClass);
		if (mapping == null) {
			throw new ProjectionError("Projection " + (collection ? (Class<Object>)targetComponentClass : (Class<Object>)targetClass).getSimpleName() + " is missing from index.");
		}
		return mapping;
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
