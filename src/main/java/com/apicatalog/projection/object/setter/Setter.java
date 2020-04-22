package com.apicatalog.projection.object.setter;

import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;

public interface Setter {

	void set(Object object, Object value) throws ObjectError;
	
	ObjectType getType();
	
	String getName();
}
