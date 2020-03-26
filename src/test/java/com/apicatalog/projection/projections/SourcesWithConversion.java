package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converter.std.Concat;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;

@Projection(ObjectBasicTypes.class)
public class SourcesWithConversion {

	@Sources(value =  {
					@Source(value = "longValue"),
					@Source(type=ObjectReference.class, value = "stringValue")
				},
			map = @Conversion(type = Concat.class, value="!@#")
		)
	public String longstring;
		

	@Sources(value =  {
					@Source(type=ObjectReference.class, value = "stringValue"),
					@Source("longValue")
				},
			map = @Conversion(type = Concat.class)
		)
	public String stringlong;	
}
