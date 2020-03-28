package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.Object1;

@Projection(Object1.class)
public class Object1To {

	public String id;
	
	public Object2To object2;

}
