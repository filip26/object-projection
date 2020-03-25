package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converter.std.UrlPatternFnc;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;

@Projection
public class ProjectionUrlPatternFnc {

	@Sources(value =  {
					@Source(type=ObjectBasicTypes.class, value = "longValue"),
					@Source(type=ObjectReference.class, value = "stringValue")
				},
			map = @Conversion(type = UrlPatternFnc.class, value="https://www.example.org/{longValue}/{stringValue}")
		)
	public String href;
		
}
