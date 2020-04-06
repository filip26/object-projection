package com.apicatalog.projection.source;

import java.util.Optional;

public final class SourceType {

	final Class<?> type;
	final Class<?> componentType;
	final String name;
	
	SourceType(String name, Class<?> type, Class<?> componentType) {
		this.type = type;
		this.name = name;
		this.componentType = componentType;
	}

	public static SourceType of(Class<?> type) {
		return of(null, type, null);
	}

	public static SourceType of(String qualifier, Class<?> type) {
		return of(qualifier, type, null);
	}

	public static SourceType of(Class<?> type, Class<?> componentType) {
		return new SourceType(null, type, componentType);
	}

	public static SourceType of(String qualifier, Class<?> type, Class<?> componentType) {
		return new SourceType(qualifier, type, componentType);
	}

	public Class<?> getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> getComponentType() {
		return componentType;
	}

	@Override
	public String toString() {
		return "NamedType [type=" + Optional.ofNullable(type).map(Class::getSimpleName).orElse("n/a") + ", name=" + Optional.ofNullable(name).orElse("n/a") + "]";
	}
}
