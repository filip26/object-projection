package com.apicatalog.projection.object.setter;

import java.util.Map;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.object.ObjectType;

public final class MapEntrySetter implements Setter {

	final String name;
	
	final ObjectType type;
	
	protected MapEntrySetter(final String name, final ObjectType type) {
		this.name = name;
		this.type = type;
	}
	
	public static final MapEntrySetter from(final String name, final ObjectType type) {		
		return new MapEntrySetter(name, type);
	}

	@Override
	public void set(final Object object, final Object value) throws ProjectionError {

		if ((object == null) || !Map.class.isInstance(object)) {
			return;
		}
		
		@SuppressWarnings("unchecked")
		final Map<Object, Object> map = (Map<Object, Object>)object;

		map.put(name, value);
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
		return "MapEntrySetter [name=" + name + ", type=" + type + "]";
	}
}