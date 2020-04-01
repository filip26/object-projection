package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface PropertyMapping {

	String getName();
		
	SourceMapping getSource();
	
	TargetMapping getTarget();
	
	Object compose(ProjectionQueue path, ContextObjects context) throws ProjectionError;
	
	void decompose(Object value, ContextObjects context) throws ProjectionError;
	
	boolean isVisible(int level);
}
