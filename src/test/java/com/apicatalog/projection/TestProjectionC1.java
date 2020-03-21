package com.apicatalog.projection;

import java.util.Collection;

import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Provider;

@ObjectProjection
public class TestProjectionC1 {

	@Provider(type=TestCollectionObject.class)
	Collection<TestProjectionA> items;
	
	
}
