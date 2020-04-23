package com.apicatalog.projection.projections.invalid;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Object1;

@Projection(BasicTypes.class)
public class SourceConversionErrorTo {


	@Source
	public Object1 longValue;
	
}
