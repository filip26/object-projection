package com.apicatalog.projection;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Source;

@ObjectProjection(TestObjectA.class)
public class TestProjectionAC {

	@Source("instantValue")
	Long projectedLong;
	
	@Source("longValue")
	String projectedString;
	
	@Source("stringValue")
	Boolean projectedBoolean;
	
}
