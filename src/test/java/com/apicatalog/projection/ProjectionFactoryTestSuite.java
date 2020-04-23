package com.apicatalog.projection;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.apicatalog.projection.annotated.AccessModeTest;
import com.apicatalog.projection.annotated.ArrayCollectorTest;
import com.apicatalog.projection.annotated.ConstantTest;
import com.apicatalog.projection.annotated.CycleCheckTest;
import com.apicatalog.projection.annotated.DirectMappingTest;
import com.apicatalog.projection.annotated.Object2ArrayTest;
import com.apicatalog.projection.annotated.Object2CollectionTest;
import com.apicatalog.projection.annotated.ImplicitConversionTest;
import com.apicatalog.projection.annotated.InterfaceTest;
import com.apicatalog.projection.annotated.MixedObjectsTest;
import com.apicatalog.projection.annotated.NegativeTest;
import com.apicatalog.projection.annotated.OneToOneWithFncTest;
import com.apicatalog.projection.annotated.PrefixMappingTest;
import com.apicatalog.projection.annotated.PropertyNameOverrideTest;
import com.apicatalog.projection.annotated.ProvidedCollectionTest;
import com.apicatalog.projection.annotated.ProvidedObjectTest;
import com.apicatalog.projection.annotated.ProvidedRefArrayTest;
import com.apicatalog.projection.annotated.ProvidedRefCollectionTest;
import com.apicatalog.projection.annotated.ProvidedRefTest;
import com.apicatalog.projection.annotated.RefCollectionTest;
import com.apicatalog.projection.annotated.ReferenceCompositeTest;
import com.apicatalog.projection.annotated.SourcesWithConversionTest;
import com.apicatalog.projection.annotated.TwoSourceCompositeTest;
import com.apicatalog.projection.annotated.UriTemplateConversionTest;
import com.apicatalog.projection.annotated.VisibilityTest;

@RunWith(Suite.class)
@SuiteClasses({
	AccessModeTest.class,
	ArrayCollectorTest.class,
	ConstantTest.class,
	CycleCheckTest.class,
	DirectMappingTest.class,
	ImplicitConversionTest.class,
	InterfaceTest.class,
	MixedObjectsTest.class,
	NegativeTest.class,
	Object2ArrayTest.class,
	Object2CollectionTest.class,
	OneToOneWithFncTest.class,
	PrefixMappingTest.class,
	PropertyNameOverrideTest.class,
	ProvidedCollectionTest.class,
	ProvidedObjectTest.class,
	ProvidedRefArrayTest.class,
	ProvidedRefCollectionTest.class,
	ProvidedRefTest.class,
	RefCollectionTest.class,
	ReferenceCompositeTest.class,
	SourcesWithConversionTest.class,
	TwoSourceCompositeTest.class,
	UriTemplateConversionTest.class,
	VisibilityTest.class	
})
public class ProjectionFactoryTestSuite {

}
