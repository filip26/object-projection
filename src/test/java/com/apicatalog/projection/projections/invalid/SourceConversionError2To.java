package com.apicatalog.projection.projections.invalid;

import java.net.URI;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class SourceConversionError2To {


	@Source
	public URI stringValue;
	
}
