package com.apicatalog.projection.property.source;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ProjectionContext;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface Source  {

	Object read(ProjectionQueue queue, ProjectionContext context) throws ProjectionError;
		
	void write(ProjectionQueue queue, Object object, ProjectionContext context) throws ProjectionError;

	boolean isReadable();

	boolean isWritable();
	
	ObjectType getTargetType();

}
