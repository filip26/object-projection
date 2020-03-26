package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.SimpleObject;

@Projection(ObjectBasicTypes.class)
public class ProvidedObjectProperty {

	@Source("stringValue")
	public String title;
	
	@Provided
	public SimpleObject object;
	
	
}
