package com.apicatalog.projection.property.source;

import java.util.Collection;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public interface SourceWriter  {

	void write(ExtractionContext context, Object object) throws ProjectionError;

	ObjectType getTargetType();
	
	boolean isAnyTypeOf(SourceType...sourceTypes);

	Collection<SourceType> getSourceTypes();

}
