package com.apicatalog.projection.source;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public final class SourceType {

	final Class<?> type;
	final Class<?> componentType;
	final String name;
	
	protected SourceType(final String name, final Class<?> type, final Class<?> componentType) {
		this.type = type;
		this.name = name;
		this.componentType = componentType;
	}

	public static SourceType of(final Class<?> type) {
		return of(null, type, null);
	}

	public static SourceType of(final String qualifier, final Class<?> type) {
		return of(qualifier, type, null);
	}

	public static SourceType of(final Class<?> type, final Class<?> componentType) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		return new SourceType(null, type, componentType);
	}

	public static SourceType of(final String qualifier, final Class<?> type, final Class<?> componentType) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		return new SourceType(qualifier, type, componentType);
	}
	
	public static SourceType of(final Object object) {
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

	public boolean isAssignableFrom(final SourceType sourceType) {
		return isAssignableFrom(sourceType.getName(), sourceType.getType(), sourceType.getComponentType());
	}

	public boolean isAssignableFrom(final String qualifier, final Class<?> objectType, final Class<?> objectComponentType) {
		return qualifierMatch(qualifier) 
					&& objectType.isAssignableFrom(type)
					&& ((objectComponentType == null)
							|| (objectComponentType.isAssignableFrom(componentType))
							);
	}

	public boolean isInstance(final String qualifier, final Object object) {
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
	public boolean equals(final Object obj) {
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
