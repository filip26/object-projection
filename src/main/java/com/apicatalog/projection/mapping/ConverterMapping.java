package com.apicatalog.projection.mapping;

import com.apicatalog.projection.converter.Converter;

@Deprecated
public class ConverterMapping {

	Class<? extends Converter<?, ?>> converterClass;
	
	Class<?> sourceClass;
	Class<?> sourceComponentClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	public Class<? extends Converter<?, ?>> getConverterClass() {
		return converterClass;
	}

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
	
	public void setConverterClass(Class<? extends Converter<? , ?>> converterClass) {
		this.converterClass = converterClass;
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
}
