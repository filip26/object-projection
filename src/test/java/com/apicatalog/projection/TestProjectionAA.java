package com.apicatalog.projection;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Provider;

@ObjectProjection
public class TestProjectionAA {

	@Provider(type=TestObjectAA.class, property = "objectA")
	TestProjectionA a;
	
}
