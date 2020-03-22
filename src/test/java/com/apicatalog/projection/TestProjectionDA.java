package com.apicatalog.projection;

import java.time.Instant;

import com.apicatalog.projection.annotation.ObjectProjection;

@ObjectProjection(TestObjectA.class)
public class TestProjectionDA {

	Long longValue;

	String stringValue;
	
	Boolean booleanValue;
	
	Instant instantValue;
	
	Double doubleValue;
	
	transient String doNotMapMe;
}
