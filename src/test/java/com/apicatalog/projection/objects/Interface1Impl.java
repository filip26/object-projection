package com.apicatalog.projection.objects;

public class Interface1Impl implements Interface1 {

	String id;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
}
