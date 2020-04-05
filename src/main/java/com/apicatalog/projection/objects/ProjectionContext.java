package com.apicatalog.projection.objects;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProjectionContext {

	final Logger logger = LoggerFactory.getLogger(ProjectionContext.class);
	
	final Map<ObjectKey, Object> index;
	
	final ContextNamespace namespace;
	
	protected ProjectionContext(Map<ObjectKey, Object> index, Collection<Object> objects) {
		this.index = index;
		this.namespace = new ContextNamespace();
	}

	public ProjectionContext(ProjectionContext context) {
		this.index = new LinkedHashMap<>(context.index);
		this.namespace = new ContextNamespace(context.namespace);
	}
	
	public static final ProjectionContext of(Object...objects) {		
		return new ProjectionContext(index(objects), Arrays.asList(objects));
	}
	
	public Object get(final Class<?> clazz, final String name) {
		//FIXME optimize
		
		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);
				
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
	
	public ProjectionContext addOrReplace(Object object, String qualifier) {
		index.put(ObjectKey.of(object.getClass(), qualifier), object);
		return this;
	}

	public boolean contains(Class<?> objectClass, String qualifier) {
		return index.containsKey(ObjectKey.of(objectClass, qualifier));
	}
	
	public void pushNamespace(String name) {
		this.namespace.push(name);
	}

	public int size() {
		return index.size();
	}
	
	protected static final Map<ObjectKey, Object> index(Object[] objects) {
		//TODO extract superclasses/interfaces and index 
		return Stream
				.of(objects)
				.collect(Collectors.toMap(
							ObjectKey::of,
								o -> 
								(NamedObject.class.isInstance(o))
									? ((NamedObject<?>)o).getObject()
									: o
									));
	}
}
