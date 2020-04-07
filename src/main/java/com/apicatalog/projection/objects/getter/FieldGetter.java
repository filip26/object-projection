package com.apicatalog.projection.objects.getter;

import java.lang.reflect.Field;
import java.util.Optional;

import com.apicatalog.projection.objects.ObjectType;

public final class FieldGetter implements Getter {

	final Field field;
	
	final ObjectType type;
		
	protected FieldGetter(Field field, ObjectType type) {
		this.field = field;
		this.type = type;
	}
	
	public static final FieldGetter from(Field field, ObjectType type) {
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
