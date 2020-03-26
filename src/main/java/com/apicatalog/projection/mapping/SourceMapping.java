package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.objects.SourceObjects;

public interface SourceMapping {

	Object compose(SourceObjects sources) throws ProjectionError, ConvertorError;

	void decompose(Object[] objects, SourceObjects sources) throws ProjectionError, ConvertorError;
}
