package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.SimpleObject;

@Projection(SimpleObject.class)
public class SimpleObjectTo {

	public String s1;
	
	public Integer i1;
	
}