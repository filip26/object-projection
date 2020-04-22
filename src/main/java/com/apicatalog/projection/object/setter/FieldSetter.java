package com.apicatalog.projection.object.setter;

import java.lang.reflect.Field;

import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;

public final class FieldSetter implements Setter {

	final Field field;
	
	final ObjectType type; 

	protected FieldSetter(final Field field, final ObjectType type) {
		this.field = field;
		this.type = type;
	}
	
	public static final FieldSetter from(final Field field, final ObjectType type) {
		return new FieldSetter(field, type);
	}

	@Override
	public void set(final Object object, final Object value) throws ObjectError {
		
		if (object == null) {
			throw new IllegalArgumentException();
		}
		
		try {
			field.setAccessible(true);
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ObjectError("Can not set value " + value + " to " + object.getClass().getCanonicalName() + "." + field.getName(), e);
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
	
	@Override
	public String toString() {
		return "FieldSetter [name=" + (field != null ? field.getName() : "n/a") + ", type=" + type + "]";
	}
}