package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converters.UriTemplate;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;

@Projection
public class UriTemplateConversion {

	@Sources(value =  {
					@Source(type=BasicTypes.class, value = "longValue"),
					@Source(type=Reference.class, value = "stringValue")
				},
			map = @Conversion(type = UriTemplate.class, value="https://www.example.org/{}/{stringValue}")
		)
	public String href;
		
}
