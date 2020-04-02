package com.apicatalog.projection.beans;

import com.apicatalog.projection.ProjectionError;

public interface Setter {

	void set(Object object, Object value) throws ProjectionError;
	
	Class<?> getValueClass();
	Class<?> getValueComponentClass();

	Object getName();
}
