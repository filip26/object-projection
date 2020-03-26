package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class ProvidedRefProperty {

	@Source("stringValue")
	public String title;
	
	@Provided
	public SimpleObjectProjection projection;
	
	
}
