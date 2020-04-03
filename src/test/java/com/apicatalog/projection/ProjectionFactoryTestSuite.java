package com.apicatalog.projection;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.apicatalog.projection.factory.AccessModeTest;
import com.apicatalog.projection.factory.ArrayCollectorTest;
import com.apicatalog.projection.factory.ConstantTest;
import com.apicatalog.projection.factory.CycleCheckTest;
import com.apicatalog.projection.factory.DirectMappingTest;
import com.apicatalog.projection.factory.ImplicitConversionTest;
import com.apicatalog.projection.factory.OneToOneWithFncTest;
import com.apicatalog.projection.factory.PropertyNameOverrideTest;
import com.apicatalog.projection.factory.ProvidedCollectionTest;
import com.apicatalog.projection.factory.ProvidedObjectTest;
import com.apicatalog.projection.factory.ProvidedRefCollectionTest;
import com.apicatalog.projection.factory.ProvidedRefTest;
import com.apicatalog.projection.factory.RefCollectionTest;
import com.apicatalog.projection.factory.ReferenceCompositeTest;
import com.apicatalog.projection.factory.SourcesWithConversionTest;
import com.apicatalog.projection.factory.UriTemplateConversionTest;
import com.apicatalog.projection.factory.VisibilityTest;

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
public class ProjectionFactoryTestSuite {

}
