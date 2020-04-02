package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.SimpleObject;

@Projection(BasicTypes.class)
public class ProvidedObjectTo {

	@Source("stringValue")
	public String title;
	
	@Provided
	public SimpleObject object;

	@Override
	public String toString() {
		return "ProvidedObjectTo [title=" + title + ", object=" + object + "]";
	}	
}
