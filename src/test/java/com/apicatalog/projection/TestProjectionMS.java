package com.apicatalog.projection;

import com.apicatalog.projection.annotation.IFunction;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.ifnc.Concat;

@Projection
public class TestProjectionMS {

	@Sources(value =  {
					@Source(type=TestObjectA.class, value = "longValue"),
					@Source(type=TestObjectAA.class, value = "stringValue")
				},
			map = @IFunction(type = Concat.class)
		)
	Long projectedLong;
		

	@Sources(value =  {
					@Source(type=TestObjectAA.class, value = "stringValue"),
					@Source(type=TestObjectA.class, value = "longValue")
				},
			map = @IFunction(type = Concat.class)
		)
	String inheritedValue;

	
}
