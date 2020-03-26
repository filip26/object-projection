package com.apicatalog.projection.projections;

import java.time.Instant;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class BasicTypesImplicitConversion {

	@Source("floatValue")
	public Integer integerValue;
	
	@Source("instantValue")
	public Long longValue;

	@Source("longValue")
	public String stringValue;
	
	@Source("integerValue")
	public Boolean booleanValue;
	
	public Instant instantValue;

	@Source("booleanValue")
	public Float floatValue;
	
	@Source("stringValue")
	public Double doubleValue;

}
