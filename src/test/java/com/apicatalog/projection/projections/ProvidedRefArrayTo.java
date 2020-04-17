package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class ProvidedRefArrayTo {

	@Provided(name = "items")
	public SimpleObjectTo[] items;
	
	
}
