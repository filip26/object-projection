package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public interface PropertyMapping {

	String getName();
		
	SourceMapping getSource();
	
	TargetMapping getTarget();
	
	Object compose(Path path, ContextObjects context) throws ProjectionError;
	
	void decompose(Path path, Object value, ContextObjects context) throws ProjectionError;
	
	boolean isVisible(int level);
	
}
