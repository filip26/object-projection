package com.apicatalog.projection.object.setter;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.object.ObjectType;

public interface Setter {

	void set(Object object, Object value) throws ProjectionError;
	
	ObjectType getType();
	
	String getName();
}
