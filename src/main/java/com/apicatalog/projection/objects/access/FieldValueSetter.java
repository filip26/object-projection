package com.apicatalog.projection.objects.access;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.objects.ObjectUtils;

public class FieldValueSetter implements ValueSetter {

	final TypeAdapters typeAdapters;
	
	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	String fieldName;
	
	public FieldValueSetter(final TypeAdapters typeAdapters) {
		this.typeAdapters = typeAdapters;
	}

	@Override
	public void set(final Object object, final Object value) throws ProjectionError {
		ObjectUtils.setPropertyValue(
				object, 
				fieldName,
				typeAdapters.convert(valueClass, valueComponentClass, value)
				);
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
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
