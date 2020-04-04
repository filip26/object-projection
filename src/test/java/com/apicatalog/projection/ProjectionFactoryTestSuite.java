package com.apicatalog.projection;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.apicatalog.projection.annotated.AccessModeTest;
import com.apicatalog.projection.annotated.ArrayCollectorTest;
import com.apicatalog.projection.annotated.ConstantTest;
import com.apicatalog.projection.annotated.CycleCheckTest;
import com.apicatalog.projection.annotated.DirectMappingTest;
import com.apicatalog.projection.annotated.ImplicitConversionTest;
import com.apicatalog.projection.annotated.OneToOneWithFncTest;
import com.apicatalog.projection.annotated.PropertyNameOverrideTest;
import com.apicatalog.projection.annotated.ProvidedCollectionTest;
import com.apicatalog.projection.annotated.ProvidedObjectTest;
import com.apicatalog.projection.annotated.ProvidedRefCollectionTest;
import com.apicatalog.projection.annotated.ProvidedRefTest;
import com.apicatalog.projection.annotated.RefCollectionTest;
import com.apicatalog.projection.annotated.ReferenceCompositeTest;
import com.apicatalog.projection.annotated.SourcesWithConversionTest;
import com.apicatalog.projection.annotated.UriTemplateConversionTest;
import com.apicatalog.projection.annotated.VisibilityTest;

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
