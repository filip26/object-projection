package com.apicatalog.projection.beans;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import com.apicatalog.projection.ProjectionError;

public class FieldSetter implements Setter {

	Class<?> valueClass;
	Class<?> valueComponentClass;

	final Field field;

	protected FieldSetter(Field field) {
		this.field = field;
	}
	
	public static final FieldSetter from(Field field) {
		
		final FieldSetter setter = new FieldSetter(field);
		
		setter.setValueClass(field.getType());

		if (Collection.class.isAssignableFrom(field.getType())) {
			setter.setValueComponentClass((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
		}
		
		return setter;
	}


	@Override
	public void set(final Object object, final Object value) throws ProjectionError {
		
		try {
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ProjectionError(e);
		}
		
//		ObjectUtils.setPropertyValue(
//				object, 
//				fieldName,
//				typeAdapters.convert(valueClass, valueComponentClass, value)
//				);
	}

	@Override
	public Class<?> getValueClass() {
		return valueClass;
	}

	@Override
	public Class<?> getValueComponentClass() {
		return valueComponentClass;
	}
	
	public void setValueClass(final Class<?> valueClass) {
		this.valueClass = valueClass;
	}
	
	public void setValueComponentClass(final Class<?> valueComponentClass) {
		this.valueComponentClass = valueComponentClass;
	}
	
	@Override
	public Object getName() {
		return field.getName();
	}	
}
