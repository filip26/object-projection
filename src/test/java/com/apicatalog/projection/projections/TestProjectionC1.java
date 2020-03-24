package com.apicatalog.projection.projections;

import java.util.Collection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.TestCollectionObject;

@Projection(TestCollectionObject.class)
public class TestProjectionC1 {

	public Collection<ProjectionBasicTypesNameOverride> items;

}
