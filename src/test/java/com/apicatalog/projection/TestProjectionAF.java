package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Function;
import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Provider;
import com.apicatalog.projection.fnc.Append;

@ObjectProjection
public class TestProjectionAF {

	@Provider(
			type=TestObjectA.class,
			property = "stringValue"
			)
	String originString;
	
	@Provider(
			type=TestObjectA.class,
			property = "stringValue",
			map = @Function(type = Append.class, value="GHIJKL")
			)
	String modifiedString;

	@Provider(
			type=TestObjectA.class,
			property = "stringValue",
			map = {
				@Function(type = Append.class, value="GHIJKL"),
				@Function(type = Append.class, value="MNOPQR")
			}
			)
	String modified2xString;

}
