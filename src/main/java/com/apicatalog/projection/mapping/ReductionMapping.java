package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;

public interface ReductionMapping {

	Object reduce(Object...objects) throws ProjectionError;
	
	Object[] expand(Object object) throws ProjectionError;

	ReducerMapping getReducerMapping();
}
