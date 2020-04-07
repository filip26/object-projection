package com.apicatalog.projection.objects.setter;

import java.lang.reflect.Field;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ObjectType;

public final class FieldSetter implements Setter {

	final Field field;
	
	final ObjectType type; 

	protected FieldSetter(Field field, ObjectType type) {
		this.field = field;
		this.type = type;
	}
	
	public static final FieldSetter from(Field field, ObjectType type) {
		return new FieldSetter(field, type);
	}

	@Override
	public void set(final Object object, final Object value) throws ProjectionError {	
		try {
			field.setAccessible(true);
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ProjectionError(e);
		}
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
