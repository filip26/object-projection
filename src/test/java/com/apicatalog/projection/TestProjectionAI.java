package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;

@Projection
public class TestProjectionAI {

	@Source(type=TestObjectA.class, value = "longValue")
	Long projectedLong;
		
	@Source(type=TestObjectAA.class, value = "stringValue")
	String inheritedValue;

}
