package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.converter.std.Concat;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class TestProjectionAF {

	@Source( "stringValue")
	public String originString;
	
	@Source(
			value = "stringValue",
			map = @Conversion(type = Concat.class, value="GHIJKL")
			)
	public String modifiedString;

	@Source(
			value = "stringValue",
			map = {
				@Conversion(type = Concat.class, value="GHIJKL"),
				@Conversion(type = Concat.class, value="MNOPQR")
			}
			)
	public String modified2xString;

	public String nodirectmappingtosourceproperty;
	
}