package com.apicatalog.projection;

import java.util.Collection;

import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.source.SourceType;

public interface ProjectionExtractor<P> {

	void extract(P projection, ExtractionContext context) throws CompositionError;
	
	Collection<SourceType> getSourceTypes();
	
	Collection<String> getDependencies();

	String getProjectionName();

}
