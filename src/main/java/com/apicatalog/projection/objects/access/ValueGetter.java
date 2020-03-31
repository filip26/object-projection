package com.apicatalog.projection.objects.access;

import com.apicatalog.projection.ProjectionError;

public interface ValueGetter {

	Object get(Object object) throws ProjectionError;
	
	Class<?> getValueClass();
	Class<?> getValueComponentClass();

}