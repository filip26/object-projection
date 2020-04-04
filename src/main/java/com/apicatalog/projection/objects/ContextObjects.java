package com.apicatalog.projection.objects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextObjects {

	final Logger logger = LoggerFactory.getLogger(ContextObjects.class);
	
	final Map<ObjectKey, Object> index;

	final ContextNamespace namespace;
	
	protected ContextObjects(Map<ObjectKey, Object> index) {
		this.index = index;
		this.namespace = new ContextNamespace();
	}
	
	public ContextObjects(ContextObjects sources) {
		this.index = new LinkedHashMap<>(sources.index);
		this.namespace = new ContextNamespace(sources.namespace);
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
		//FIXME optimize
		
		String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);
				
		return Optional.ofNullable(index.get(ObjectKey.of(clazz, qualifiedName)))
				.orElseGet(() -> {
					for (Map.Entry<ObjectKey, Object> entry : index.entrySet()) {
						if (clazz.isAssignableFrom(entry.getKey().getClazz()) && ((StringUtils.isBlank(qualifiedName) && StringUtils.isBlank(entry.getKey().getQualifier()))
									|| StringUtils.isNotBlank(qualifiedName) && qualifiedName.equals(entry.getKey().getQualifier())
									)) {
								return entry.getValue();
						
						}
					}
					return null;
				});
	}

	public Object[] getValues() {
		return index.entrySet()
					.stream()
					.map(e -> StringUtils.isBlank(e.getKey().qualifier) 
								? e.getValue() 
								: NamedObject.of(e.getKey().qualifier, e.getValue()
										)
						
						)
					.collect(Collectors.toList()).toArray(new Object[0]);
	}
	
	public ContextObjects addOrReplace(Object object, String qualifier) {
		index.put(ObjectKey.of(object.getClass(), qualifier), object);
		return this;
	}

	public boolean contains(Class<? extends Object> class1, String qualifier) {
		return index.containsKey(ObjectKey.of(class1, qualifier));
	}
	
	public void pushNamespace(String name) {
		this.namespace.push(name);
	}

	public int size() {
		return index.size();
	}
}
