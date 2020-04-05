package com.apicatalog.projection.converter;

import com.apicatalog.projection.objects.ObjectType;

public class ConverterMapping {

	Converter<Object, Object> converter;
	
	ObjectType sourceType;
	
	ObjectType targetType;
	
	public Converter<Object, Object> getConverter() {
		return converter;
	}
	public void setConverter(Converter<Object, Object> converter) {
		this.converter = converter;
	}
	public ObjectType getSourceType() {
		return sourceType;
	}
	public void setSourceType(ObjectType sourceType) {
		this.sourceType = sourceType;
	}
	public ObjectType getTargetType() {
		return targetType;
	}
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}
}
