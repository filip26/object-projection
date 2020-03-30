package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.Interface1;

@Projection
public class Interface1To {

	@Source(type = Interface1.class, value="id")
	public Long id;
	
	
}
