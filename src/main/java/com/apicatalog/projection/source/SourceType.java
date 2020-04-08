package com.apicatalog.projection.source;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public final class SourceType {

	final Class<?> type;
	final Class<?> componentType;
	final String name;
	
	protected SourceType(String name, Class<?> type, Class<?> componentType) {
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
	
	public static SourceType of(Object object) {
		if (SourceObject.class.isInstance(object)) {
			final SourceObject sourceObject = (SourceObject)object;
			
			return of(sourceObject.getName(), sourceObject.getObject().getClass());	
		}
		return of(object.getClass(), null);
	}

	public Class<?> getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SourceType [type=" + Optional.ofNullable(type).map(Class::getSimpleName).orElse("n/a") + ", name=" + Optional.ofNullable(name).orElse("n/a") + "]";
	}

	public boolean isAssignableFrom(SourceType sourceType) {
		return isAssignableFrom(sourceType.getName(), sourceType.getType(), sourceType.getComponentType());
	}

	public boolean isAssignableFrom(String qualifier, Class<?> objectType, Class<?> objectComponentType) {
		return qualifierMatch(qualifier) 
					&& objectType.isAssignableFrom(type)
					&& ((objectComponentType == null)
							|| (objectComponentType.isAssignableFrom(componentType))
							);
	}

	public boolean isInstance(String qualifier, Object object) {
		return qualifierMatch(qualifier) && type.isInstance(object); 
	}
	
	public Class<?> getComponentType() {
		return componentType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
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
		return Objects.equals(name, other.name) && Objects.equals(type, other.type);
	}
	
	final boolean qualifierMatch(String qualifier) {
		return StringUtils.isNotBlank(name) ? name.equals(qualifier) : StringUtils.isBlank(qualifier);
	}
}
