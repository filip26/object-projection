package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converter.std.Concatenation;
import com.apicatalog.projection.converter.std.Suffix;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;

@Projection(BasicTypes.class)
public class SourcesReduceMapTo {

	@Sources(value =  {
					@Source(value = "longValue"),
					@Source(type=Reference.class, value = "stringValue")
				},
			reduce = @Reduction(type = Concatenation.class),
			map = @Conversion(type = Suffix.class, value = "!@#")
		)
	public String longstring;
		

	@Sources(value =  {
					@Source(type=Reference.class, value = "stringValue"),
					@Source("longValue")
				},
			reduce = @Reduction(type = Concatenation.class)
		)
	public String stringlong;	
}