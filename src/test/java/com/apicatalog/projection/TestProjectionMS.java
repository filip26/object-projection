package com.apicatalog.projection;

import com.apicatalog.projection.annotation.Function;
import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.fnc.Concat;

@ObjectProjection
public class TestProjectionMS {

	@Sources(value =  {
					@Source(type=TestObjectA.class, value = "longValue"),
					@Source(type=TestObjectAA.class, value = "stringValue")
				},
			map = @Function(type = Concat.class)
		)
	Long projectedLong;
		

	@Sources(value =  {
					@Source(type=TestObjectAA.class, value = "stringValue"),
					@Source(type=TestObjectA.class, value = "longValue")
				},
			map = @Function(type = Concat.class)
		)
	String inheritedValue;

	
}
