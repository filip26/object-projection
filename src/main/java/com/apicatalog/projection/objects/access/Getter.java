package com.apicatalog.projection.objects.access;

import com.apicatalog.projection.ProjectionError;

public interface Getter {

	Object get(Object object) throws ProjectionError;
	
	Class<?> getValueClass();
	Class<?> getValueComponentClass();

	String getName();

}
