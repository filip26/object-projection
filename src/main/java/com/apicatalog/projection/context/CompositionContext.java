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
	
	final Map<SourceType, Object> typeIndex;
	final Map<String, Object> nameIndex;
	
	final ContextNamespace namespace;
	
	protected CompositionContext(final Map<SourceType, Object> typeIndex, final Map<String, Object> nameIndex) {
		this.typeIndex = typeIndex;
		this.nameIndex = nameIndex;
		this.namespace = new ContextNamespace();
	}

	public CompositionContext(final CompositionContext context) {
		this.typeIndex = new LinkedHashMap<>(context.typeIndex);
		this.nameIndex = new LinkedHashMap<>(context.nameIndex);
		this.namespace = new ContextNamespace(context.namespace);
	}
	
	public static final CompositionContext of(Object...objects) {		
		return new CompositionContext(typeIndex(objects), nameIndex(objects));
	}
	
	public Optional<Object> get(SourceType sourceType) {
		return get(sourceType.getName(), sourceType.getType());
	}
	
	public Optional<Object> get(final String name, final Class<?> clazz) {
		
		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);
				
		return Optional.ofNullable(typeIndex.get(SourceType.of(qualifiedName, clazz)))
				.or(() -> {
					for (Map.Entry<SourceType, Object> entry : typeIndex.entrySet()) {
						if (clazz.isAssignableFrom(entry.getKey().getType()) && ((StringUtils.isBlank(qualifiedName) && StringUtils.isBlank(entry.getKey().getName()))
									|| StringUtils.isNotBlank(qualifiedName) && qualifiedName.equals(entry.getKey().getName())
									)) {
								return Optional.ofNullable(entry.getValue());
						
						}
					}
					return Optional.empty();
				});
	}
	
	public Optional<Object> get(final String name) {
		final String qualifiedName = Optional.ofNullable(name).map(n -> namespace.getQName(name)).orElse(null);

		return Optional.ofNullable(nameIndex.get(qualifiedName));
	}
	
	public Stream<Object> stream() {
		return typeIndex.entrySet()
				.stream()
				.map(e -> StringUtils.isBlank(e.getKey().getName()) 
							? e.getValue() 
							: SourceObject.of(e.getKey().getName(), e.getValue()
									)
					);		
	}

	public CompositionContext put(final Object object) {
		return put(null, object);
	}
	
	public CompositionContext put(final String name, final Object object) {
		typeIndex.put(SourceType.of(name, object.getClass()), object);
		return this;
	}

	public void namespace(String name) {
		this.namespace.push(name);
	}

	public int size() {
		return typeIndex.size();
	}
	
	protected static final Map<SourceType, Object> typeIndex(Object[] objects) {
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
	
	protected static final Map<String, Object> nameIndex(Object[] objects) {
		return Stream
				.of(objects)
				.filter(SourceObject.class::isInstance)
				.map(SourceObject.class::cast)
				.collect(Collectors.toMap(
							SourceObject::getName,
							SourceObject::getObject 
									));
	}
}
