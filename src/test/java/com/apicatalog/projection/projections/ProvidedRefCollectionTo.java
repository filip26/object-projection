package com.apicatalog.projection.projections;

import java.util.Collection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class ProvidedRefCollectionTo {

	@Provided(qualifier = "items")
	public Collection<SimpleObjectTo> items;
	
	
}
