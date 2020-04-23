package com.apicatalog.projection.projections.invalid;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class UnmappableSourcePropertyTo {


	@Source
	public String unknown;
	
}
