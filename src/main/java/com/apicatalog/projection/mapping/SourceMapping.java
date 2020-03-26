package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.SourceObjects;

public interface SourceMapping {

	Object compose(SourceObjects sources) throws ProjectionError, ConverterError;

	void decompose(Object[] objects, SourceObjects sources) throws ProjectionError, ConverterError;
}
