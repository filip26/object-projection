package com.apicatalog.projection;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Source;

@ObjectProjection
public class TestProjectionAI {

	@Source(type=TestObjectA.class, value = "longValue")
	Long projectedLong;
		
	@Source(type=TestObjectAA.class, value = "stringValue")
	String inheritedValue;

}
