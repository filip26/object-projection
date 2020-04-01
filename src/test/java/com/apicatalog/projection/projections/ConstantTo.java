package com.apicatalog.projection.projections;

import java.util.Arrays;
import java.util.Collection;

import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.Object1;

@Projection(Object1.class)
public class ConstantTo {

	public String id;
	
	@Constant("1234567890")
	public Long longValue;

	@Constant({ "s1", "s2", "s3" })
	public String[] stringArray;
	
	@Constant({ "true", "false", "true", "true"  })
	public Collection<Boolean> booleanCollection;

	@Override
	public String toString() {
		return "ConstantTo [id=" + id + ", longValue=" + longValue + ", stringArray=" + Arrays.toString(stringArray)
				+ ", booleanCollection=" + booleanCollection + "]";
	}
}
