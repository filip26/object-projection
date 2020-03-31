package com.apicatalog.projection.objects.access;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ObjectUtils;

public class FieldValueGetter implements ValueGetter {

	Class<?> valueClass;
	Class<?> valueComponentClass;
	
	String fieldName;
	
	@Override
	public Object get(final Object object) throws ProjectionError {
		return ObjectUtils.getPropertyValue(object, fieldName);
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return fieldName;
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
}
