package com.apicatalog.projection.objects;

import java.time.Instant;
import java.util.Collection;

public class BasicTypes {

	public Integer integerValue;
	
	public Long longValue;
	
	public String stringValue;
	
	public Boolean booleanValue;
	
	public Instant instantValue;
	
	public Float floatValue;
	
	public Double doubleValue;
	
	public String[] stringArray;
	
	public Collection<String> stringCollection;

	@Override
	public String toString() {
		return "BasicTypes [integerValue=" + integerValue + ", longValue=" + longValue + ", stringValue=" + stringValue
				+ ", booleanValue=" + booleanValue + ", instantValue=" + instantValue + ", floatValue=" + floatValue
				+ ", doubleValue=" + doubleValue 
				+  "]";
	}
}
