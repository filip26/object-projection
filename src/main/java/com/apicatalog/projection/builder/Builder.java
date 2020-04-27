package com.apicatalog.projection.builder;

import java.util.Optional;

import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.object.ObjectType;

public interface Builder<T> {

	Optional<T> build(TypeConversions typeConverters) throws ProjectionError;
	
	Builder<T> targetType(ObjectType targetType);
	
	Builder<T> targetProjection(String targetProjectionName);
}
