package com.apicatalog.projection.objects.access;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import com.apicatalog.projection.ProjectionError;

public class FieldGetter implements Getter {

	final Field field;
	
	Class<?> valueClass;
	Class<?> valueComponentClass;
		
	protected FieldGetter(Field field) {
		this.field = field;
	}
	
	public static final FieldGetter from(Field field) {
		
		final FieldGetter getter = new FieldGetter(field);
		
		getter.setValueClass(field.getType());

		if (Collection.class.isAssignableFrom(field.getType())) {
			getter.setValueComponentClass((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
		}
		
		return getter;
	}
	
	@Override
	public Object get(final Object object) throws ProjectionError {
		try {
			return field.get(object);
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			
			throw new ProjectionError(e);
		}
	}

	@Override
	public Class<?> getValueClass() {
		return valueClass;
	}
	
	@Override
	public Class<?> getValueComponentClass() {
		return valueComponentClass;
	}
	
	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}
	
	public void setValueComponentClass(Class<?> valueComponentClass) {
		this.valueComponentClass = valueComponentClass;
	}

	@Override
	public String getName() {
		return field.getName();
	}	
}
