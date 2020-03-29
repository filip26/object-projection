package com.apicatalog.projection.mapping;

import com.apicatalog.projection.converter.Converter;

public class ConverterMapping {

	Class<? extends Converter<?, ?>> converterClass;
	
	Class<?> sourceClass;
	Class<?> targetClass;
	
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
}
