package com.apicatalog.projection.projections;

import java.util.Collection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class ProvidedRefCollectionTo {

	@Provided(name = "items")
	public Collection<SimpleObjectTo> items;
	
	
}
