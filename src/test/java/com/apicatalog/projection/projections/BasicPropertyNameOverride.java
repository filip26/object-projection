package com.apicatalog.projection.projections;

import java.time.Instant;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection
public class BasicPropertyNameOverride {

	@Source(type = ObjectBasicTypes.class, value = "longValue")
	public Long projectedLong;
	
	@Source(type = ObjectBasicTypes.class, value = "stringValue")
	public String projectedString;
	
	@Source(type = ObjectBasicTypes.class, value = "booleanValue")
	public Boolean projectedBoolean;
	
	@Source(type = ObjectBasicTypes.class, value = "instantValue")
	public Instant projectedInstant;
	
	@Source(type = ObjectBasicTypes.class, value = "doubleValue")
	public Double projectedDouble;
	
}
