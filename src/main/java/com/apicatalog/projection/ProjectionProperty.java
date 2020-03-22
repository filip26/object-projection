package com.apicatalog.projection;

import com.apicatalog.projection.fnc.InvertibleFunction;

public class ProjectionProperty {

	final String name;
	
	Class<?> targetClass;
	Class<?> itemClass;
	
	PropertyMapping[] mapping;
	
	public ProjectionProperty(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public PropertyMapping[] getMapping() {
		return mapping;
	}

	public void setMapping(PropertyMapping[] mapping) {
		this.mapping = mapping;
	}

	public InvertibleFunction[] getFunctions() {
		// TODO Auto-generated method stub
		return new InvertibleFunction[0];
	}

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
}
