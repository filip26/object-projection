package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;

@Projection
public class CompositeTo {

	@Source(type=BasicTypes.class, value = "longValue")
	public Long source1;
		
	@Source(type=Reference.class, value = "stringValue")
	public String source2;

}
