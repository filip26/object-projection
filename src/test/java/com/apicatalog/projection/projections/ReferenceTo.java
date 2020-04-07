package com.apicatalog.projection.projections;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.objects.Reference;

@Projection(Reference.class)
public class ReferenceTo {

	@Source("objectA")
	public CompositeTo ref;

	@Override
	public String toString() {
		return "ReferenceTo [ref=" + ref + "]";
	}
}
