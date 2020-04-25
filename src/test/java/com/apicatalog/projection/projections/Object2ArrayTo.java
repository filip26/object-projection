package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.converters.UriTemplate;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Env;

@Projection
public class Object2ArrayTo {

	@Source(
			type = Env.class, value = "baseUri", mode = AccessMode.READ_ONLY,
			map = @Conversion(type = UriTemplate.class, value = "{baseUri}/a/b/c"),
			optional = true
	)
	public String href;

	@Source(type = BasicTypes.class, value = "booleanValue", optional = true)
	public Long[] longArray;

	@Source(type = BasicTypes.class, value = "stringValue", optional = true)
	public String[] stringArray;
}
