package com.apicatalog.projection.objects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceObjects {

	final Logger logger = LoggerFactory.getLogger(SourceObjects.class);
	
	final Map<ProjectedObjectKey, Object> index;
	
	protected SourceObjects(Map<ProjectedObjectKey, Object> index) {
		this.index = index;
	}
	
	public SourceObjects(SourceObjects sources) {
		this.index = new LinkedHashMap<>(sources.index);
	}
	
	public static SourceObjects of(Object...objects) {		
		return new SourceObjects(Stream
							.of(objects)
							.collect(Collectors.toMap(
										ProjectedObjectKey::of,
										o -> o
									)));		
	}
	
	public Object get(Class<?> clazz, String name) {
		return index.get(ProjectedObjectKey.of(clazz, name));
	}

	public Object[] getValues() {
		return index.values().toArray(new Object[0]);
	}
	
	public void addOrReplace(Object object) {
		index.put(ProjectedObjectKey.of(object), object);
	}
}
