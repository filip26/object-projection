package com.apicatalog.projection;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Source;

@ObjectProjection(TestObjectAA.class)
public class TestProjectionAA {

	@Source(value = "objectA")
	TestProjectionAI a;
	
}
