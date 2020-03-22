package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;

@Projection(TestObjectA.class)
public class TestProjectionAC {

	@Source("instantValue")
	Long projectedLong;
	
	@Source("longValue")
	String projectedString;
	
	@Source("stringValue")
	Boolean projectedBoolean;
	
}