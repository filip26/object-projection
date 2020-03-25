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
	
	//TODO use scanner to get a list of objects at initialization time 
//	@Deprecated
//	public static SourceObjects from(ProjectionMapping<?> projection) throws ProjectionError {
//
//		final Map<ProjectedObjectKey, Object> index = new LinkedHashMap<>();
//		
//		for (PropertyMapping property : projection.getProperties()) {
//			
//			if (property.getSource() == null) {
//				continue;
//			}
//			SourceMapping mapping = property.getSource();
////			for (SourceMappingImpl mapping : property.getSources()) {
////FIXME				
////				final ProjectedObjectKey key = ProjectedObjectKey.of(mapping.getSourceClass(), mapping.getQualifier());
////				
////				if (index.containsKey(key)) {
////					continue;
////				}
////			
////				final Object object = ProjectionFactory.newInstance(mapping.getSourceClass());
////				if (object != null) {
////					index.put(key, object);
////				}
////			}
//		}
//		
//		return new SourceObjects(index);
//	}
	
	public Object[] getValues() {
		return index.values().toArray(new Object[0]);
	}
	
	public void addOrReplace(Object object) {
		index.put(ProjectedObjectKey.of(object), object);
	}

	public void merge(Object object) {
		ProjectedObjectKey key = ProjectedObjectKey.of(object);

		addOrReplace(object);	//FIXME hack
	}	
}
