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
	EmbeddedOneToOneTest.class,
	ImplicitConversionTest.class,
	MultipleSourcesTest.class,
	OneToOneWithFncTest.class,
	UrlPatternFncTest.class,	
})
public class AllTestsSuite {

}
