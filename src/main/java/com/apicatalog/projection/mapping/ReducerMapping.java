package com.apicatalog.projection.mapping;

import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.reducer.Reducer;


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
