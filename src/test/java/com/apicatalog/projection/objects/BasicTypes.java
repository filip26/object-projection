package com.apicatalog.projection.objects;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class BasicTypes {

	public Integer integerValue;
	
	public Long longValue;
	
	public String stringValue;
	
	public Boolean booleanValue;
	
	public Instant instantValue;
	
	public Date dateValue;
	
	public Float floatValue;
	
	public Double doubleValue;
	
	public String[] stringArray;
	
	public Collection<String> stringCollection;

	@Override
	public String toString() {
		return "BasicTypes [integerValue=" + integerValue + ", longValue=" + longValue + ", stringValue=" + stringValue
				+ ", booleanValue=" + booleanValue + ", instantValue=" + instantValue + ", dateValue=" + dateValue
				+ ", floatValue=" + floatValue + ", doubleValue=" + doubleValue + ", stringArray="
				+ Arrays.toString(stringArray) + ", stringCollection=" + stringCollection + "]";
	}
}
