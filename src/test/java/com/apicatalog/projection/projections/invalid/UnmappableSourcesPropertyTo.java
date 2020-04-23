package com.apicatalog.projection.projections.invalid;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class UnmappableSourcesPropertyTo {


	@Sources(
			value = @Source
			)
	public String unknown;
	
}
