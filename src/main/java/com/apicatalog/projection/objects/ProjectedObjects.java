package com.apicatalog.projection.objects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;

public class ProjectedObjects {

	final Map<ProjectedObjectKey, Object> index;
	
	protected ProjectedObjects(Map<ProjectedObjectKey, Object> index) {
		this.index = index;
	}
	
	public static ProjectedObjects of(Object[] objects) {		
		return new ProjectedObjects(Stream
							.of(objects)
							.collect(Collectors.toMap(
										o -> ProjectedObjectKey.of(o.getClass(), null),
										o -> o
									)));		
	}
	
	public Object get(Class<?> clazz, String qualifier) {
		return index.get(ProjectedObjectKey.of(clazz, qualifier));
	}
	
	//TODO use scanner to get a list of objects at initialization time 
	@Deprecated
	public static ProjectedObjects from(ProjectionMapping projection) throws ProjectionError {
		
		final Map<ProjectedObjectKey, Object> index = new LinkedHashMap<>();
		
		for (PropertyMapping property : projection.getProperties()) {
			
			for (SourceMapping mapping : property.getSources()) {
				
				final ProjectedObjectKey key = ProjectedObjectKey.of(mapping.getObjectClass(), mapping.getQualifier());
				
				if (index.containsKey(key)) {
					continue;
				}
			
				final Object object = ProjectionFactory.newInstance(mapping.getObjectClass());
				if (object != null) {
					index.put(key, object);
				}
			}
		}
		
		return new ProjectedObjects(index);
	}
	
	public Object[] getValues() {
		return index.values().toArray(new Object[0]);
	}

	public void merge(Object object, String qualifier) {
		
		Object orig = index.get(ProjectedObjectKey.of(object.getClass(), qualifier));
		//FIXME
		index.put(ProjectedObjectKey.of(object.getClass(), qualifier), object);
	}
		
}