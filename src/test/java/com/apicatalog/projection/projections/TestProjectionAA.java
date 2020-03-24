package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectReference;

@Projection(ObjectReference.class)
public class TestProjectionAA {

	@Source(value = "objectA")
	public TestProjectionAI ai;
	
	@Provided
	public ProjectionBasicTypesNameOverride aa;
	
}
