package com.apicatalog.projection.projections;

import java.time.Instant;
import java.util.Collection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class ImplicitConversionTo {

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
	
	@Source("stringArray")
	public Collection<String> stringCollection;

	@Source("stringCollection")
	public Long[] longArray;
	
}
