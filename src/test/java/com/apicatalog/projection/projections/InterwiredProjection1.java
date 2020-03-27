package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.InterwiredObject1;

@Projection(InterwiredObject1.class)
public class InterwiredProjection1 {

	public String id;
	
	public InterwiredProjection2 object2;

}
