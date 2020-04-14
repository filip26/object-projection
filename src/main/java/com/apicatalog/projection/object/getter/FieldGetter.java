package com.apicatalog.projection.object.getter;

import java.lang.reflect.Field;
import java.util.Optional;

import com.apicatalog.projection.object.ObjectType;

public final class FieldGetter implements Getter {

	final Field field;
	
	final ObjectType type;
		
	protected FieldGetter(final Field field, final ObjectType type) {
		this.field = field;
		this.type = type;
	}
	
	public static final FieldGetter from(final Field field, final ObjectType type) {
		return new FieldGetter(field, type);
	}
	
	@Override
	public Optional<Object> get(final Object object) {
		try {
			field.setAccessible(true);
			return Optional.ofNullable(field.get(object));
			
		} catch (IllegalArgumentException | IllegalAccessException e)  { /* ignore */}

		return Optional.empty();
	}

	@Override
	public String getName() {
		return field.getName();
	}
	
	@Override
	public ObjectType getType() {
		return type;
	}
}
