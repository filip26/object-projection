package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.converter.std.Suffix;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class TestProjectionAF {

	@Source( "stringValue")
	public String originString;
	
	@Source(
			value = "stringValue",
			map = @Conversion(type = Suffix.class, value="GHIJKL")
			)
	public String modifiedString;

	@Source(
			value = "stringValue",
			map = {
				@Conversion(type = Suffix.class, value="GHIJKL"),
				@Conversion(type = Suffix.class, value="MNOPQR")
			}
			)
	public String modified2xString;

	public String nodirectmappingtosourceproperty;
	
}