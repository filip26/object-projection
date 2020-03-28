package com.apicatalog.projection.converter;

public class ConverterMapping<S, T> {

	Class<? extends Converter<S, T>> converterClass;
	
	Class<S> sourceClass;
	Class<T> targetClass;
	
	public Class<? extends Converter<S, T>> getConverterClass() {
		return converterClass;
	}

	public Class<S> getSourceClass() {
		return sourceClass;
	}

	public Class<T> getTargetClass() {
		return targetClass;
	}
	
	public void setSourceClass(Class<S> sourceClass) {
		this.sourceClass = sourceClass;
	}
	
	public void setTargetClass(Class<T> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void setConverterClass(Class<? extends Converter<S , T>> converterClass) {
		this.converterClass = converterClass;
	}
}
