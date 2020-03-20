package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.apicatalog.projection.annotation.Provider;

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
	
	public static ProjectedObjects from(Projection projection) throws ProjectionError {
		
		final Map<ProjectedObjectKey, Object> index = new LinkedHashMap<>();
		
		for (ProjectionProperty property : projection.getProperties()) {
			
			for (Provider provider : property.getProviders()) {
				
				final ProjectedObjectKey key = ProjectedObjectKey.of(provider.type(), provider.qualifier());
				
				if (index.containsKey(key)) {
					continue;
				}
			
				final Object object = ProjectionFactory.newInstance(provider.type());
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
		
}
