package com.apicatalog.projection;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.apicatalog.projection.mapper.MapperTest;

@RunWith(Suite.class)
@SuiteClasses({
	MapperTest.class,
	
	DirectMappingTest.class,
	PropertyNameOverrideTest.class,
	CollectionTest.class,
	ReferenceCompositeTest.class,
	ImplicitConversionTest.class,
	SourcesWithConversionTest.class,
	OneToOneWithFncTest.class,
	UriTemplateConversionTest.class,
	ProvidedObjectTest.class,
	ProvidedRefTest.class,
	CycleCheckTest.class,
	ProvidedCollectionTest.class,
	VisibilityTest.class,
	ConstantTest.class,
	ProvidedRefCollectionTest.class,
	AccessModeTest.class,
})
public class ProjectionTestSuite {

}
