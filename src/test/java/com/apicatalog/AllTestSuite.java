package com.apicatalog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.apicatalog.projection.MapProjectionBuilderTest;
import com.apicatalog.projection.ObjectProjectionBuilderTest;
import com.apicatalog.projection.ProjectionFactoryTestSuite;
import com.apicatalog.projection.mapper.ProjectionMapperTest;
import com.apicatalog.uritemplate.UriTemplateTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	ProjectionMapperTest.class,
	ObjectProjectionBuilderTest.class,
	MapProjectionBuilderTest.class,
	UriTemplateTestSuite.class,
	ProjectionFactoryTestSuite.class
})
public class AllTestSuite {

}
