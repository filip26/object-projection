package com.apicatalog.projection.projections;

import java.time.Instant;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class TypeObjectTo {

	public Integer integerValue;
	
	public Long longValue;

	public String stringValue;
	
	public Boolean booleanValue;
	
	public Instant instantValue;
	
	public Float floatValue;
	
	public Double doubleValue;

}
