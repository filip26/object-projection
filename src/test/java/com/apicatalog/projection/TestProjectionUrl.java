package com.apicatalog.projection;

import com.apicatalog.projection.annotation.IFunction;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.ifnc.std.UrlPatternFnc;

@Projection
public class TestProjectionUrl {

	@Sources(value =  {
					@Source(type=TestObjectA.class, value = "longValue"),
					@Source(type=TestObjectAA.class, value = "stringValue")
				},
			map = @IFunction(type = UrlPatternFnc.class, value="https://www.example.org/{longValue}/{stringValue}")
		)
	String href;
		
}
