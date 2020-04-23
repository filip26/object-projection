package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.converters.UriTemplate;
import com.apicatalog.projection.objects.Env;

@Projection
public class Object2ArrayTo {

	@Source(
			type = Env.class, value = "baseUri", mode = AccessMode.READ_ONLY, 
			map = @Conversion(type = UriTemplate.class, value = "{baseUri}" + "/a/b/c")
	)
	public String href;
	
}
