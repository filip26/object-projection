package com.apicatalog.projection.context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.objects.NamedObject;

public final class ProjectionContext {

	final Logger logger = LoggerFactory.getLogger(ProjectionContext.class);
	
	final Map<ContextIndex, Object> index;
	
	final ContextNamespace namespace;
	
	protected ProjectionContext(Map<ContextIndex, Object> index) {
		this.index = index;
		this.namespace = new ContextNamespace();
	}

	public ProjectionContext(ProjectionContext context) {
		this.index = new LinkedHashMap<>(context.index);
		this.namespace = new ContextNamespace(context.namespace);
	}
	
	public static final ProjectionContext of(Object...objects) {		
		return new ProjectionContext(index(objects));
	}
	
	public Object get(final Class<?> clazz, final String name) {
		
		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);
				
		return Optional.ofNullable(index.get(ContextIndex.of(clazz, qualifiedName)))
				.orElseGet(() -> {
					for (Map.Entry<ContextIndex, Object> entry : index.entrySet()) {
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
		return stream().collect(Collectors.toList()).toArray(new Object[0]);
	}
	
	public Stream<Object> stream() {
		return index.entrySet()
				.stream()
				.map(e -> StringUtils.isBlank(e.getKey().qualifier) 
							? e.getValue() 
							: NamedObject.of(e.getKey().qualifier, e.getValue()
									)
					
					);		
	}

	public ProjectionContext addOrReplace(Object object) {
		return addOrReplace(object, null);
	}
	
	public ProjectionContext addOrReplace(Object object, String qualifier) {
		index.put(ContextIndex.of(object.getClass(), qualifier), object);
		return this;
	}

	public boolean contains(Class<?> objectClass, String qualifier) {
		return index.containsKey(ContextIndex.of(objectClass, qualifier));
	}
	
	public void namespace(String name) {
		this.namespace.push(name);
	}

	public int size() {
		return index.size();
	}
	
	protected static final Map<ContextIndex, Object> index(Object[] objects) {
		return Stream
				.of(objects)
				.collect(Collectors.toMap(
							ContextIndex::of,
								o -> 
								(NamedObject.class.isInstance(o))
									? ((NamedObject<?>)o).getObject()
									: o
									));
	}	
}
