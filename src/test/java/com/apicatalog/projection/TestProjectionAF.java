package com.apicatalog.projection;

import com.apicatalog.projection.annotation.IFunction;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.ifnc.Concat;

@Projection(TestObjectA.class)
public class TestProjectionAF {

	@Source( "stringValue")
	String originString;
	
	@Source(
			value = "stringValue",
			map = @IFunction(type = Concat.class, value="GHIJKL")
			)
	String modifiedString;

	@Source(
			value = "stringValue",
			map = {
				@IFunction(type = Concat.class, value="GHIJKL"),
				@IFunction(type = Concat.class, value="MNOPQR")
			}
			)
	String modified2xString;

}
