package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converter.std.UriTemplate;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;

@Projection
public class UriTemplateConversion {

	@Sources(value =  {
					@Source(type=ObjectBasicTypes.class, value = "longValue"),
					@Source(type=ObjectReference.class, value = "stringValue")
				},
			map = @Conversion(type = UriTemplate.class, value="https://www.example.org/{}/{stringValue}")
		)
	public String href;
		
}
