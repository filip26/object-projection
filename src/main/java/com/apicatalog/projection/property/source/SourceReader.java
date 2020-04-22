package com.apicatalog.projection.property.source;

import java.util.Collection;
import java.util.Optional;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public interface SourceReader  {

	Optional<Object> read(CompositionContext context) throws CompositionError;
		
	ObjectType getTargetType();
	
	Collection<SourceType> getSourceTypes();
}
