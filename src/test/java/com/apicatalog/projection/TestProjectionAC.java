package com.apicatalog.projection;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Provider;

@ObjectProjection
public class TestProjectionAC {

	@Provider(type=TestObjectA.class, property = "instantValue")
	Long projectedLong;
	
	@Provider(type=TestObjectA.class, property = "longValue")
	String projectedString;
	
	@Provider(type=TestObjectA.class, property = "stringValue")
	Boolean projectedBoolean;
	
}
