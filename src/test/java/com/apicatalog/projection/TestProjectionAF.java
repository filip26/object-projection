package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Function;
import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.fnc.Concat;

@ObjectProjection(TestObjectA.class)
public class TestProjectionAF {

	@Source( "stringValue")
	String originString;
	
	@Source(
			value = "stringValue",
			map = @Function(type = Concat.class, value="GHIJKL")
			)
	String modifiedString;

	@Source(
			value = "stringValue",
			map = {
				@Function(type = Concat.class, value="GHIJKL"),
				@Function(type = Concat.class, value="MNOPQR")
			}
			)
	String modified2xString;

}
