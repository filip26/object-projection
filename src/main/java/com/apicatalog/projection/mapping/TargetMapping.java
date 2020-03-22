package com.apicatalog.projection.mapping;

public class TargetMapping {

	Class<?> targetClass;
	Class<?> itemClass;

	boolean reference;
	
	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public void setItemClass(Class<?> itemClass) {
		this.itemClass = itemClass;
	}
	
	public Class<?> getItemClass() {
		return itemClass;
	}
	
	public boolean isCollection() {
		return itemClass != null;
	}
	
	public boolean isReference() {
		return reference;
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}
}
