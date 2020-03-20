package com.apicatalog.projection;

public class Value {

	final String id;
	Object object;
	
	Value(Object value, String id) {
		this.object = value;
		this.id = id;
	}
	
	public static Value of(Object object, String id) {
		return new Value(object, id);
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	public Object getObject() {
		return object;
	}
	
	public String getId() {
		return id;
	}

}
