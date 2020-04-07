package com.apicatalog.projection.context;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.source.SourceType;

public final class ExtractionContext {

	final Logger logger = LoggerFactory.getLogger(ExtractionContext.class);

	public static final int MAX_OBJECTS = 10;
	
	SourceType[] types;
	Object[] objects;
	int index;
	
	protected ExtractionContext() {
		this.types = new SourceType[MAX_OBJECTS];
		this.objects = new Object[MAX_OBJECTS];
		this.index = 0;
	}
	
	public static final ExtractionContext newInstance() {		
		return new ExtractionContext();
	}

	public ExtractionContext accept(String qualifier, Class<?> objectType, Class<?> componentType) {

		objects[index] = null;
		types[index++] = SourceType.of(qualifier, objectType, componentType);
		
		return this;
	}
	
	public void set(final String qualifier, final Object object) {
		
		if (index == 0) {
			throw new IllegalStateException();
		}

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isInstance(qualifier, object)) {
				
				if (logger.isDebugEnabled()) {
					logger.debug("Set {}, {}",
							Optional.ofNullable(qualifier).orElse("n/a"),
							object
							);
				}

				objects[i] = object;
				return;
			}
		}
		
		logger.trace("Rejected to set {}, qualifier = {}", object.getClass().getSimpleName(), qualifier);
	}
	
	public Object get(final String qualifier, final Class<?> objectType, final Class<?> componentType) {

		if (index == 0) {
			throw new IllegalStateException();
		}

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifier, objectType, componentType)) {
				return objects[i];
			}
		}
		return null;
	}
	
	public Object remove(final String qualifier, final Class<?> objectType, final Class<?> componentType) {
		if (index == 0) {
			throw new IllegalStateException();
		}

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifier, objectType, componentType)) {
				index--;

				return objects[i];
			}
		}

		return null;
	}

	public int size() {
		return index;
	}

	public boolean isAccepted(String qualifier, Class<?> objectType, Class<?> componentType) {
		if (index == 0) {
			throw new IllegalStateException();
		}

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifier, objectType, componentType)) {
				return true;
			}
		}	
		return false;
	}
	
	@Deprecated(forRemoval = true, since = "v0.8")
	public SourceType[] types() {
		return Arrays.copyOf(types, index);
	}

	public Class<?> getAssignableType(String qualifier, Class<?> objectType, Class<?> componentType) {

		if (index == 0) {
			throw new IllegalStateException();
		}

		for (int i=index - 1; i >= 0; i--) {
			if (types[i].isAssignableFrom(qualifier, objectType, componentType)) {
				return types[i].getType();
			}
		}
		return null;
	}
}
