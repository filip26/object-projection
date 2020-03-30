package com.apicatalog.projection.objects;

import com.apicatalog.projection.ProjectionError;

public interface ValueSetter {

	void set(Object object, Object value) throws ProjectionError;
	
	Class<?> getValueClass();
	Class<?> getValueComponentClass();
}
