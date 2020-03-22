package com.apicatalog.projection;

import java.time.Instant;

import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Projection;

@Projection(TestObjectA.class)
public class TestProjectionA {

	@Source("longValue")
	Long projectedLong;
	
	@Source("stringValue")
	String projectedString;
	
	@Source("booleanValue")
	Boolean projectedBoolean;
	
	@Source("instantValue")
	Instant projectedInstant;
	
	@Source("doubleValue")
	Double projectedDouble;
}
