package com.apicatalog.projection.projections;

import java.time.Instant;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection
public class BasicTypes2 {

	@Source(type=ObjectBasicTypes.class)
	public Long longValue;

	@Source(type=ObjectBasicTypes.class)
	public String stringValue;
	
	@Source(type=ObjectBasicTypes.class)
	public Boolean booleanValue;
	
	@Source(type=ObjectBasicTypes.class)
	public Instant instantValue;
	
	@Source(type=ObjectBasicTypes.class)
	public Double doubleValue;

}
