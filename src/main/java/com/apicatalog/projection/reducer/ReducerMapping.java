package com.apicatalog.projection.reducer;

import com.apicatalog.projection.objects.ObjectType;


public class ReducerMapping {

	Reducer<Object, Object> reducer;
	
	ObjectType sourceType;
	ObjectType targetType;
	
	public Reducer<Object, Object> getReducer() {
		return reducer;
	}
	
	public void setReducer(Reducer<Object, Object> reducer) {
		this.reducer = reducer;
	}
	
	public void setSourceType(ObjectType sourceType) {
		this.sourceType = sourceType;
	}
	
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}
	
	public ObjectType getSourceType() {
		return sourceType;
	}
	
	public ObjectType getTargetType() {
		return targetType;
	}
}
