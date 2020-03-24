package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.IFunction;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.ifnc.std.ConcatFnc;
import com.apicatalog.projection.objects.ObjectBasicTypes;

@Projection(ObjectBasicTypes.class)
public class TestProjectionAF {

	@Source( "stringValue")
	public String originString;
	
	@Source(
			value = "stringValue",
			map = @IFunction(type = ConcatFnc.class, value="GHIJKL")
			)
	public String modifiedString;

	@Source(
			value = "stringValue",
			map = {
				@IFunction(type = ConcatFnc.class, value="GHIJKL"),
				@IFunction(type = ConcatFnc.class, value="MNOPQR")
			}
			)
	public String modified2xString;

	public String nodirectmappingtosourceproperty;
	
}