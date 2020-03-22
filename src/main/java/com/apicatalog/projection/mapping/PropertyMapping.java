package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ifnc.InvertibleFunction;

public class PropertyMapping {

	final String name;
	
	Class<?> targetClass;
	Class<?> itemClass;
	
	SourceMapping[] sources;
	
	InvertibleFunction<?>[] functions;
	
	public PropertyMapping(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public SourceMapping[] getSources() {
		return sources;
	}

	public void setSources(SourceMapping[] mapping) {
		this.sources = mapping;
	}

	public InvertibleFunction<?>[] getFunctions() {
		return functions;
	}
	
	public void setFunctions(InvertibleFunction<?>[] functions) {
		this.functions = functions;
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
