package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class AccessModeTo {

	@Source(mode = AccessMode.READ_ONLY)
	public String stringValue;

	@Source(mode = AccessMode.WRITE_ONLY)
	public Long longValue;
	
	@Source(mode = AccessMode.READ_WRITE)
	public Boolean booleanValue;
}
