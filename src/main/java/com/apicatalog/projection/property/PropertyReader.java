package com.apicatalog.projection.property;

import java.util.Collection;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.source.SourceType;

public interface PropertyReader extends Property {

	void read(ProjectionStack stack, ExtractionContext context) throws CompositionError;
	
	String getDependency();
	
	Collection<SourceType> getSourceTypes();
	
}