package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public interface SourceMapping {

	Object compose(Path path, ContextObjects context) throws ProjectionError;

	void decompose(Path path, Object[] objects, ContextObjects context) throws ProjectionError;
}
