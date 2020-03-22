package com.apicatalog.projection;

import java.util.Collection;

import com.apicatalog.projection.annotation.ObjectProjection;

@ObjectProjection(TestCollectionObject.class)
public class TestProjectionC1 {

	Collection<TestProjectionA> items;

}
