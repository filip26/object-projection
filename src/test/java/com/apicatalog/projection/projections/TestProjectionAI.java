package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;

@Projection
public class TestProjectionAI {

	@Source(type=ObjectBasicTypes.class, value = "longValue")
	public Long projectedLong;
		
	@Source(type=ObjectReference.class, value = "stringValue")
	public String inheritedValue;

}
