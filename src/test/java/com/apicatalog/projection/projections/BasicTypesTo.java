package com.apicatalog.projection.projections;

import java.time.Instant;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection
public class BasicTypesTo {

	@Source(type=BasicTypes.class)
	public Long longValue;

	@Source(type=BasicTypes.class)
	public String stringValue;
	
	@Source(type=BasicTypes.class)
	public Boolean booleanValue;
	
	@Source(type=BasicTypes.class)
	public Instant instantValue;
	
	@Source(type=BasicTypes.class)
	public Double doubleValue;

	@Source(type=BasicTypes.class)
	public Integer integerValue;
}
