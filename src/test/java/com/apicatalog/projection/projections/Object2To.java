package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.objects.Object2;

@Projection(Object2.class)
public class Object2To {

	@Visibility(level= 0)
	public String id;

	public TypeObjectTo object3;

}
