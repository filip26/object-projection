package com.apicatalog.projection.property.target;

import java.util.Optional;

import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.object.ObjectType;

public interface TargetExtractor {

	Optional<Object> extract(ObjectType objectType, Object projection, ExtractionContext context) throws ExtractionError;

	String getProjectionName();
	
}
