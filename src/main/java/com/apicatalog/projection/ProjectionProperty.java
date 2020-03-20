package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Provider;
import com.apicatalog.projection.fnc.InvertibleFunction;

public class ProjectionProperty {

	final String name;
	
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
		return null;
	}



}
