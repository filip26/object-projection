package com.apicatalog.projection.objects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.NamedObject;

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
										o -> 
											(NamedObject.class.isInstance(o)) 
												? ((NamedObject<?>)o).getObject()
												: o
									)));		
	}
	
	public Object get(Class<?> clazz, String name) {
		Object object =  index.get(ObjectKey.of(clazz, name));
		
		//FIXME optimize
		if (object == null) {
			for (Map.Entry<ObjectKey, Object> entry : index.entrySet()) {
				if (clazz.isAssignableFrom(entry.getKey().getClazz()) && ((StringUtils.isBlank(name) && StringUtils.isBlank(entry.getKey().getQualifier()))
							|| StringUtils.isNotBlank(name) && name.equals(entry.getKey().getQualifier())
							)) {
						return entry.getValue();
				
				}
			}
		}
		return object;
	}

	public Object[] getValues() {
		return index.values().toArray(new Object[0]);
	}
	
	public ContextObjects addOrReplace(Object object, String qualifier) {
		index.put(ObjectKey.of(object.getClass(), qualifier), object);
		return this;
	}

	public boolean contains(Class<? extends Object> class1, String qualifier) {
		return index.containsKey(ObjectKey.of(class1, qualifier));
	}
}
