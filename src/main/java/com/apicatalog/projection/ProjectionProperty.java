package com.apicatalog.projection;

import com.apicatalog.projection.fnc.InvertibleFunction;

public class ProjectionProperty {

	final String name;
	
	ValueProvider[] providers;
	
	public ProjectionProperty(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ValueProvider[] getProviders() {
		return providers;
	}
	
	public void setProviders(ValueProvider[] providers) {
		this.providers = providers;
	}

	public InvertibleFunction[] getFunctions() {
		// TODO Auto-generated method stub
		return null;
	}



}
