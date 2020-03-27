package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;

public interface SourceMapping {

	Object compose(ContextObjects context) throws ProjectionError;

	void decompose(Object[] objects, ContextObjects context) throws ProjectionError;
}
