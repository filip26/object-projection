package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.Object3;
import com.apicatalog.projection.objects.Object4;

@Projection(Object4.class)
public class Object4To {

	@Source(type = Object3.class)
	public String name1;
	
	public String name2;
	
	public String name3;
	
}
