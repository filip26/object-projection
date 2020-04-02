package com.apicatalog.projection.mapping;

import com.apicatalog.projection.reducer.Reducer;


public class ReductionMapping {

	Reducer<Object, Object> reducer;
	
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
	
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public void setSourceComponentClass(Class<?> sourceComponentClass) {
		this.sourceComponentClass = sourceComponentClass;
	}
	
	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
	
	public Reducer<Object, Object> getReducer() {
		return reducer;
	}
	
	public void setReducer(Reducer<Object, Object> reducer) {
		this.reducer = reducer;
	}
}
