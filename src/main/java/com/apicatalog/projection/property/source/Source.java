package com.apicatalog.projection.property.source;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface Source  {

	Object read(ProjectionQueue queue, CompositionContext context) throws ProjectionError;
		
	void write(ProjectionQueue queue, Object object, ExtractionContext context) throws ProjectionError;

	boolean isReadable();

	boolean isWritable();
	
	ObjectType getTargetType();

}
