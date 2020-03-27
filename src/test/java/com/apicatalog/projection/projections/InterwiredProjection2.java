package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.InterwiredObject2;

@Projection(InterwiredObject2.class)
public class InterwiredProjection2 {

	public String id;
	
	public InterwiredProjection1 object1;

}
