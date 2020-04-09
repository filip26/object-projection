package com.apicatalog.projection.converter;

import com.apicatalog.projection.objects.ObjectType;

public class ExplicitConverterMapping implements ConverterMapping {

	Converter<Object, Object> converter;
	
	ObjectType sourceType;
	
	ObjectType targetType;
	
	@Override
	public Converter<Object, Object> getConversion() {
		return converter;
	}
	
	public void setConverter(Converter<Object, Object> converter) {
		this.converter = converter;
	}
	
	@Override
	public ObjectType getSourceType() {
		return sourceType;
	}
	
	public void setSourceType(ObjectType sourceType) {
		this.sourceType = sourceType;
	}
	
	@Override
	public ObjectType getTargetType() {
		return targetType;
	}
	
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}
}
