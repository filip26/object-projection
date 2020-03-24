package com.apicatalog.projection.projections;

import java.time.Instant;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class ProjectionBasicTypesNameOverride2 {

	@Source("longValue")
	public Long projectedLong;
	
	@Source("stringValue")
	public String projectedString;
	
	@Source("booleanValue")
	public Boolean projectedBoolean;
	
	@Source("instantValue")
	public Instant projectedInstant;
	
	@Source("doubleValue")
	public Double projectedDouble;
	
}
