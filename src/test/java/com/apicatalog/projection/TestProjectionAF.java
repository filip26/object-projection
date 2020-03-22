package com.apicatalog.projection;

import com.apicatalog.projection.annotation.IFunction;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.ifnc.std.ConcatFnc;

@Projection(TestObjectA.class)
public class TestProjectionAF {

	@Source( "stringValue")
	String originString;
	
	@Source(
			value = "stringValue",
			map = @IFunction(type = ConcatFnc.class, value="GHIJKL")
			)
	String modifiedString;

	@Source(
			value = "stringValue",
			map = {
				@IFunction(type = ConcatFnc.class, value="GHIJKL"),
				@IFunction(type = ConcatFnc.class, value="MNOPQR")
			}
			)
	String modified2xString;

}
