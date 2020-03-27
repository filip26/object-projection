package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;

public interface PropertyMapping {

	String getName();
		
	SourceMapping getSource();
	
	TargetMapping getTarget();
	
	Object compose(int level, ContextObjects context) throws ProjectionError;
	
	void decompose(Object value, ContextObjects context) throws ProjectionError;
	
}
