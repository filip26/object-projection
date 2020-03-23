package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Embedded;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;

@Projection(TestObjectAA.class)
public class TestProjectionAA {

	@Source(value = "objectA")
	TestProjectionAI ai;
	
	@Embedded
	TestProjectionA aa;
	
}
