package com.apicatalog.projection.builder;

import java.util.Optional;

import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.object.ObjectType;

public interface Builder<T> {

	Optional<T> build(TypeConversions typeConverters) throws ProjectionBuilderError;
	
	Builder<T> targetType(ObjectType targetType, boolean targetReference);
}
