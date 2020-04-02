package com.apicatalog.projection.source;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;

public interface Source  {

	Object read(ProjectionQueue queue, ContextObjects context) throws ProjectionError;
		
	void write(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError;

	boolean isReadable();

	boolean isWritable();

}
