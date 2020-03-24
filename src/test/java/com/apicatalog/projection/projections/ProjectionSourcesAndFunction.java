package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.IFunction;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.ifnc.std.ConcatFnc;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;

@Projection(ObjectBasicTypes.class)
public class ProjectionSourcesAndFunction {

	@Sources(value =  {
					@Source(value = "longValue"),
					@Source(type=ObjectReference.class, value = "stringValue")
				},
			map = @IFunction(type = ConcatFnc.class, value="!@#")
		)
	public String longstring;
		

	@Sources(value =  {
					@Source(type=ObjectReference.class, value = "stringValue"),
					@Source("longValue")
				},
			map = @IFunction(type = ConcatFnc.class)
		)
	public String stringlong;	
}
