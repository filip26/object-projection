package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.converters.Concatenation;
import com.apicatalog.projection.converters.Suffix;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;

@Projection(BasicTypes.class)
public class SourcesReduceMapTo {

	@Sources(value =  {
					@Source(value = "longValue"),
					@Source(type=Reference.class, value = "stringValue")
				},
			
			map = {
					@Conversion(type = Concatenation.class),
					@Conversion(type = Suffix.class, value = "!@#")
			}
		)
	public String longstring;
		

	@Sources(value =  {
					@Source(type=Reference.class, value = "stringValue"),
					@Source("longValue")
				},
			map = @Conversion(type = Concatenation.class)
		)
	public String stringlong;	
}
