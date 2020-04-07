package com.apicatalog.projection.property.source;

import java.util.Optional;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.source.SourceType;

public interface Source  {

	Optional<Object> read(ProjectionQueue queue, CompositionContext context) throws ProjectionError;
		
	void write(ProjectionQueue queue, ExtractionContext context, Object object) throws ProjectionError;

	boolean isReadable();

	boolean isWritable();
	
	ObjectType getTargetType();
	
	boolean isAnyTypeOf(SourceType...sourceTypes);

}
