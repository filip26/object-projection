package com.apicatalog.projection.mapping;

import com.apicatalog.projection.converter.Converter;

public class ConverterMapping {

	Converter<Object, Object> converter;
	
	Class<?> sourceClass;
	Class<?> sourceComponentClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}
	
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	public Class<?> getSourceComponentClass() {
		return sourceComponentClass;
	}
	
	public void setSourceComponentClass(Class<?> sourceComponentClass) {
		this.sourceComponentClass = sourceComponentClass;
	}
	
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
	public Converter<Object, Object> getConverter() {
		return converter;
	}
	public void setConverter(Converter<Object, Object> converter) {
		this.converter = converter;
	}
}
