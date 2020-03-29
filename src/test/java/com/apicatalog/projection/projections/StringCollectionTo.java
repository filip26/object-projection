package com.apicatalog.projection.projections;

import java.util.Collection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;

@Projection
public class StringCollectionTo {

	@Provided(qualifier = "href")
	public String href;
	
	@Provided(qualifier = "items")
	public Collection<String> items;
	
}
