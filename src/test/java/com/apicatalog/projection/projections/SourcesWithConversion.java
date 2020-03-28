package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converter.std.Concatenation;
import com.apicatalog.projection.converter.std.Suffix;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;

@Projection(ObjectBasicTypes.class)
public class SourcesWithConversion {

	@Sources(value =  {
					@Source(value = "longValue"),
					@Source(type=ObjectReference.class, value = "stringValue")
				},
			reduce = @Reduction(type = Concatenation.class),
			map = @Conversion(type = Suffix.class, value = "!@#")
		)
	public String longstring;
		

	@Sources(value =  {
					@Source(type=ObjectReference.class, value = "stringValue"),
					@Source("longValue")
				},
			reduce = @Reduction(type = Concatenation.class)
		)
	public String stringlong;	
}
