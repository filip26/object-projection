package com.apicatalog.projection.context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.source.SourceObject;
import com.apicatalog.projection.source.SourceType;

public final class CompositionContext {

	final Logger logger = LoggerFactory.getLogger(CompositionContext.class);
	
	final Map<SourceType, Object> index;
	
	final ContextNamespace namespace;
	
	protected CompositionContext(Map<SourceType, Object> index) {
		this.index = index;
		this.namespace = new ContextNamespace();
	}

	public CompositionContext(CompositionContext context) {
		this.index = new LinkedHashMap<>(context.index);
		this.namespace = new ContextNamespace(context.namespace);
	}
	
	public static final CompositionContext of(Object...objects) {		
		return new CompositionContext(index(objects));
	}
	
	public Object get(SourceType sourceType) {
		return get(sourceType.getName(), sourceType.getType());
	}
	
	public Object get(final String name, final Class<?> clazz) {
		
		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);
				
		return Optional.ofNullable(index.get(SourceType.of(qualifiedName, clazz)))
				.orElseGet(() -> {
					for (Map.Entry<SourceType, Object> entry : index.entrySet()) {
						if (clazz.isAssignableFrom(entry.getKey().getType()) && ((StringUtils.isBlank(qualifiedName) && StringUtils.isBlank(entry.getKey().getName()))
									|| StringUtils.isNotBlank(qualifiedName) && qualifiedName.equals(entry.getKey().getName())
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
				.map(e -> StringUtils.isBlank(e.getKey().getName()) 
							? e.getValue() 
							: SourceObject.of(e.getKey().getName(), e.getValue()
									)
					);		
	}

	public CompositionContext addOrReplace(Object object) {
		return addOrReplace(object, null);
	}
	
	public CompositionContext addOrReplace(Object object, String qualifier) {
		index.put(SourceType.of(qualifier, object.getClass()), object);
		return this;
	}

	public void namespace(String name) {
		this.namespace.push(name);
	}

	public int size() {
		return index.size();
	}
	
	protected static final Map<SourceType, Object> index(Object[] objects) {
		return Stream
				.of(objects)
				.collect(Collectors.toMap(
							SourceType::of,
								o -> 
								(SourceObject.class.isInstance(o))
									? ((SourceObject)o).getObject()
									: o
									));
	}	
}
