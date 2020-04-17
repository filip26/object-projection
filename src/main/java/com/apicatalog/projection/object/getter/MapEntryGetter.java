package com.apicatalog.projection.object.getter;

import java.util.Map;
import java.util.Optional;

import com.apicatalog.projection.object.ObjectType;

public final class MapEntryGetter implements Getter {

	final String name;
	
	final ObjectType type;
	
	protected MapEntryGetter(final String name, final ObjectType type) {
		this.name = name;
		this.type = type;
	}
	
	public static final MapEntryGetter from(final String name, final ObjectType type) {
		return new MapEntryGetter(name, type);
	}

	@Override
	public Optional<Object> get(final Object object) {
		
		if ((object == null) || !Map.class.isInstance(object)) {
			return Optional.empty();
		}
		
		final Map<Object, Object> map = (Map<Object, Object>)object;

		return Optional.ofNullable(map.get(name));
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public ObjectType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "MapEntryGetter [name=" + name + ", type=" + type + "]";
	}

}
