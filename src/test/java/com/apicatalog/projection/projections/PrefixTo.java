package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.converters.Prefix;
import com.apicatalog.projection.objects.Object1;

@Projection(Object1.class)
public class PrefixTo {

	@Source(map= {@Conversion(type = Prefix.class, value = "At the beginning ")})
	public String id;


}
