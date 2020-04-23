package com.apicatalog.projection.projections;

import java.util.Collection;

import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection
public class Object2CollectionTo {

	@Source(type = BasicTypes.class, value = "doubleValue", mode = AccessMode.READ_ONLY)
	public Collection<String> collection;

}
