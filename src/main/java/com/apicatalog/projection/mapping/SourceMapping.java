package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.SourceObjects;

public interface SourceMapping {

	Object compose(SourceObjects sources) throws ProjectionError;

	void decompose(Object[] objects, SourceObjects sources) throws ProjectionError;
}
