package com.apicatalog.projection;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DirectMappingTest.class,
	PropertyNameOverrideTest.class,
	RefCollectionTest.class,
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
	ArrayCollectorTest.class,
})
public class ProjectionTestSuite {

}
