package com.apicatalog.projection.objects.setter;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ObjectType;

public interface Setter {

	void set(Object object, Object value) throws ProjectionError;
	
	ObjectType getType();
	
	String getName();
}
