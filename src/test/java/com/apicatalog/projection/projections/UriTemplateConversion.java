package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converter.std.UriTemplate;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.ObjectReference;

@Projection
public class UriTemplateConversion {

	@Sources(value =  {
					@Source(type=BasicTypes.class, value = "longValue"),
					@Source(type=ObjectReference.class, value = "stringValue")
				},
			reduce = @Reduction(type = UriTemplate.class, value="https://www.example.org/{}/{stringValue}")
		)
	public String href;
		
}
