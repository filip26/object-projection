package com.apicatalog.projection.property;

import java.util.Collection;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.source.SourceType;

public interface PropertyReader extends Property {

	void read(ProjectionStack stack, ExtractionContext context) throws ProjectionError;
	
	String getDependency();
	
	Collection<SourceType> getSourceTypes();
	
}