package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.Reference;

@Projection(Reference.class)
public class ReferenceTo {

	@Source(value = "objectA")
	public CompositeTo ref;
	
}
