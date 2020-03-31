package com.apicatalog.projection.projections;

import java.time.Instant;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection
public class NameOverrideTo {

	@Source(type = BasicTypes.class, value = "longValue")
	public Long projectedLong;
	
	@Source(type = BasicTypes.class, value = "stringValue")
	public String projectedString;
	
	@Source(type = BasicTypes.class, value = "booleanValue")
	public Boolean projectedBoolean;
	
	@Source(type = BasicTypes.class, value = "instantValue")
	public Instant projectedInstant;
	
	@Source(type = BasicTypes.class, value = "doubleValue")
	public Double projectedDouble;
	
}
