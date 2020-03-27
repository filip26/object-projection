package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.SourceObjects;

public interface PropertyMapping {

	String getName();
		
	SourceMapping getSource();
	
	TargetMapping getTarget();
	
	Object compose(int level, SourceObjects sources) throws ProjectionError;
	
	void decompose(Object value, SourceObjects sources) throws ProjectionError;
	
}
