package com.apicatalog.projection;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Provider;

@ObjectProjection
public class TestProjectionAI {

	@Provider(type=TestObjectA.class, property = "longValue")
	Long projectedLong;
		
	@Provider(type=TestObjectAA.class, property = "stringValue")
	String inheritedValue;

}
