package com.apicatalog.projection.property.source;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public interface SourceWriter  {

	void write(ProjectionStack queue, ExtractionContext context, Object object) throws ProjectionError;

	boolean isWritable();
	
	ObjectType getTargetType();
	
	boolean isAnyTypeOf(SourceType...sourceTypes);

}
