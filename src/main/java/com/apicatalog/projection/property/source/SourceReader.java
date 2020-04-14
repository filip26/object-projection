package com.apicatalog.projection.property.source;

import java.util.Optional;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.object.ObjectType;

public interface SourceReader  {

	Optional<Object> read(CompositionContext context) throws ProjectionError;
		
	ObjectType getType();

}
