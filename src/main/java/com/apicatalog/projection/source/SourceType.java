package com.apicatalog.projection.source;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

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
	
//	public Class<?> getComponentType() {
//		return componentType;
//	}

	@Override
	public String toString() {
		return "SourceType [type=" + Optional.ofNullable(type).map(Class::getSimpleName).orElse("n/a") + ", name=" + Optional.ofNullable(name).orElse("n/a") + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(/*componentType,*/ name, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SourceType other = (SourceType) obj;
		return /*Objects.equals(componentType, other.componentType) &&*/ Objects.equals(name, other.name)
				&& Objects.equals(type, other.type);
	}

	public boolean isAssignableFrom(String qualifier, Class<?> objectType, Class<?> componentType) {
		return qualifierMatch(qualifier) && type.isAssignableFrom(objectType)
				;
	}

	public boolean isInstance(String qualifier, Object object) {
		return qualifierMatch(qualifier) && type.isInstance(object); 
	}
	
	boolean qualifierMatch(String qualifier) {
		return StringUtils.isNotBlank(name) ? name.equals(qualifier) : StringUtils.isBlank(qualifier);
	}
	
	public boolean isCollection() {
		return componentType != null;
	}
	
	public Class<?> getComponentType() {
		return componentType;
	}	
}
