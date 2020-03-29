package com.apicatalog.projection.mapping;

import com.apicatalog.projection.converter.Reducer;

public class ReducerMapping {

	Class<? extends Reducer<?, ?>> reducerClass;
	
	Class<?> sourceClass;
	Class<?> sourceComponentClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	public Class<? extends Reducer<?, ?>> getReducerClass() {
		return reducerClass;
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
	
	public void setReducerClass(Class<? extends Reducer<? , ?>> converterClass) {
		this.reducerClass = converterClass;
	}
	
	public Class<?> getSourceComponentClass() {
		return sourceComponentClass;
	}
	
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public void setSourceComponentClass(Class<?> sourceComponentClass) {
		this.sourceComponentClass = sourceComponentClass;
	}
	
	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
}
