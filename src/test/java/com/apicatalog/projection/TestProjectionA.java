package com.apicatalog.projection;

import java.time.Instant;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Provider;

@ObjectProjection
public class TestProjectionA {

	@Provider(type=TestObjectA.class, property = "longValue")
	Long projectedLong;
	
	@Provider(type=TestObjectA.class, property = "stringValue")
	String projectedString;
	
	@Provider(type=TestObjectA.class, property = "booleanValue")
	Boolean projectedBoolean;
	
	@Provider(type=TestObjectA.class, property = "instantValue")
	Instant projectedInstant;
	
	@Provider(type=TestObjectA.class, property = "doubleValue")
	Double projectedDouble;
	
	@Provider(type=TestObjectA.class)
	String sameNameValue;

}
