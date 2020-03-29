package com.apicatalog.projection.objects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextObjects {

	final Logger logger = LoggerFactory.getLogger(ContextObjects.class);
	
	final Map<ObjectKey, Object> index;
	
	protected ContextObjects(Map<ObjectKey, Object> index) {
		this.index = index;
	}
	
	public ContextObjects(ContextObjects sources) {
		this.index = new LinkedHashMap<>(sources.index);
	}
	
	public static ContextObjects of(Object...objects) {		
		return new ContextObjects(Stream
							.of(objects)
							.collect(Collectors.toMap(
										ObjectKey::of,
										o -> o
									)));		
	}
	
	public Object get(Class<?> clazz, String name) {
		return index.get(ObjectKey.of(clazz, name));
	}

	public Object[] getValues() {
		return index.values().toArray(new Object[0]);
	}
	
	public ContextObjects addOrReplace(Object object) {
		index.put(ObjectKey.of(object), object);
		return this;
	}
}
