package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Provider;
import com.apicatalog.projection.fnc.InvertibleFunction;

public class ProjectionProperty {

	final String name;
	
	Class<?> targetClass;
	Class<?> itemClass;
	
	Provider[] providers;
	
	public ProjectionProperty(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Provider[] getProviders() {
		return providers;
	}
	
	public void setProviders(Provider[] providers) {
		this.providers = providers;
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
