package com.apicatalog.projection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sources {

	final Map<SourceKey, Object> index;
	
	protected Sources(Map<SourceKey, Object> index) {
		this.index = index;
	}
	
	public static Sources of(Object[] objects) {		
		return new Sources(Stream
							.of(objects)
							.collect(Collectors.toMap(
										o -> SourceKey.of(o.getClass(), null),
										o -> o
									)));		
	}
	
	public Object get(Class<?> clazz, String qualifier) {
		return index.get(SourceKey.of(clazz, qualifier));
	}
	
	public static Sources from(Projection projection) throws ProjectionError {
		
		final Map<SourceKey, Object> index = new LinkedHashMap<>();
		
		for (ProjectionProperty property : projection.getProperties()) {
			
			for (ValueProvider provider : property.getProviders()) {
				
				final SourceKey key = SourceKey.of(provider.getSourceClass(), provider.getQualifier());
				
				if (index.containsKey(key)) {
					continue;
				}
			
				final Object object = ProjectionFactory.newInstance(provider.getSourceClass());
				if (object != null) {
					index.put(key, object);
				}
			}
		}
		
		return new Sources(index);
	}
	
	public Object[] getValues() {
		return index.values().toArray(new Object[0]);
	}
		
}
