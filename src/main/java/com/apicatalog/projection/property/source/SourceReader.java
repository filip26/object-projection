package com.apicatalog.projection.property.source;

import java.util.Optional;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public interface SourceReader  {

	Optional<Object> read(ProjectionStack queue, CompositionContext context) throws ProjectionError;
		
	boolean isReadable();

	ObjectType getTargetType();
	
	boolean isAnyTypeOf(SourceType...sourceTypes);

}
