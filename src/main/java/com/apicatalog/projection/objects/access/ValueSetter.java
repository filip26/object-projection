package com.apicatalog.projection.objects.access;

import com.apicatalog.projection.ProjectionError;

public interface ValueSetter {

	void set(Object object, Object value) throws ProjectionError;
	
	Class<?> getValueClass();
	Class<?> getValueComponentClass();
}
