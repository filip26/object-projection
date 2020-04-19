package com.apicatalog.projection.property;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;

public interface PropertyReader extends Property {

	void read(ProjectionStack stack, ExtractionContext context) throws ProjectionError;

}