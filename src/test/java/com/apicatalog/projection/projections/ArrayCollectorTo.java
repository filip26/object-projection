package com.apicatalog.projection.projections;

import java.util.Collection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class ArrayCollectorTo {

	@Sources(
		value = {
			@Source("integerValue"),
			@Source("booleanValue")
		})
	public Collection<String> stringCollection;

	@Sources(
			value = {
				@Source("doubleValue"),
				@Source("booleanValue"),
				@Source("instantValue"),
				@Source("stringArray"),
			})
	public Object[] objectArray;

}
