package com.apicatalog.projection.converter;

public class ReducerMapping<S, T> {

	Class<? extends Reducer<S, T>> reducerClass;
	
	Class<S> sourceClass;
	Class<T> targetClass;
	
	public Class<? extends Reducer<S, T>> getReducerClass() {
		return reducerClass;
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
	
	public void setReducerClass(Class<? extends Reducer<S , T>> converterClass) {
		this.reducerClass = converterClass;
	}
}
