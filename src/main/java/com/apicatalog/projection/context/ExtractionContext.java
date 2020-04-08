package com.apicatalog.projection.context;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.source.SourceType;

public final class ExtractionContext {

	final Logger logger = LoggerFactory.getLogger(ExtractionContext.class);

	public static final int MAX_OBJECTS = 25;
	
	final SourceType[] types;
	final Object[] objects;
	
	int index;
	
	final ContextNamespace namespace;
	
	protected ExtractionContext() {
		this.types = new SourceType[MAX_OBJECTS];
		this.objects = new Object[MAX_OBJECTS];
		this.index = 0;
		this.namespace = new ContextNamespace();
	}
	
	public static final ExtractionContext newInstance() {		
		return new ExtractionContext();
	}

	public ExtractionContext accept(String name, Class<?> objectType, Class<?> componentType) {

		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);
		
		objects[index] = null;
		types[index++] = SourceType.of(qualifiedName, objectType, componentType);
		
		return this;
	}
	
	public void set(final String name, final Object object) {
		
		if (index == 0) {
			throw new IllegalStateException();
		}

		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);
		
		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isInstance(qualifiedName, object)) {
				
				if (logger.isDebugEnabled()) {
					logger.debug("Set {}, {}",
							Optional.ofNullable(qualifiedName).orElse("n/a"),
							object
							);
				}

				objects[i] = object;
				return;
			}
		}
		
		logger.trace("Rejected to set {}, qualifier = {}", object.getClass().getSimpleName(), qualifiedName);
	}
	
	public Optional<Object> get(final SourceType sourceType) {
		return get(sourceType.getName(), sourceType.getType(), sourceType.getComponentType());
	}
	
	public Optional<Object> get(final String name, final Class<?> objectType, final Class<?> componentType) {

		if (index == 0) {
			throw new IllegalStateException();
		}

		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifiedName, objectType, componentType)) {
				return Optional.ofNullable(objects[i]);
			}
		}
		return Optional.empty();
	}
	
	public Optional<Object> remove(final String name, final Class<?> objectType, final Class<?> componentType) {
		
		if (index == 0) {
			throw new IllegalStateException();
		}
		
		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifiedName, objectType, componentType)) {
				index--;

				return Optional.ofNullable(objects[i]);
			}
		}
		return Optional.empty();
	}

	public int size() {
		return index;
	}

	public boolean isAccepted(SourceType sourceType) {
		return isAccepted(sourceType.getName(), sourceType.getType(), sourceType.getComponentType());
	}
	
	public boolean isAccepted(String name, Class<?> objectType, Class<?> componentType) {
		if (index == 0) {
			throw new IllegalStateException();
		}

		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifiedName, objectType, componentType)) {
				return true;
			}
		}	
		return false;
	}
	
	public SourceType[] getAcceptedTypes() {
		return Arrays.copyOf(types, index);
	}

	public Optional<Class<?>> getAssignableType(final SourceType sourceType) {
		return getAssignableType(sourceType.getName(), sourceType.getType(), sourceType.getComponentType());
	}
	
	public Optional<Class<?>> getAssignableType(String qualifier, Class<?> objectType, Class<?> componentType) {

		if (index == 0) {
			throw new IllegalStateException();
		}

		final String qualifiedName = Optional.ofNullable(qualifier).map(n -> namespace.getQName(qualifier)).orElse(null);

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifiedName, objectType, componentType)) {
				return Optional.ofNullable(types[i].getType());
			}
		}
		return Optional.empty();
	}
	
	public void addNamespace(String name) {
		this.namespace.push(name);
	}
	
	public String removeLastNamespace() {
		return this.namespace.pop();
	}
}
