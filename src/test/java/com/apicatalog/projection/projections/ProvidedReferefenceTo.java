package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.BasicTypes;

@Projection(BasicTypes.class)
public class ProvidedReferefenceTo {

	@Source("stringValue")
	public String title;
	
	@Provided
	public SimpleObjectTo projection;

	@Override
	public String toString() {
		return "ProvidedReferefenceTo [title=" + title + ", projection=" + projection + "]";
	}
}
