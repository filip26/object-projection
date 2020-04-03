package com.apicatalog.projection.beans;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ObjectType;

public interface Getter {

	Object get(Object object) throws ProjectionError;
	
	ObjectType getType();
	
	String getName();

}
