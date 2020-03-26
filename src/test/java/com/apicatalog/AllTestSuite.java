package com.apicatalog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.apicatalog.projection.ProjectionTestSuite;
import com.apicatalog.uritemplate.UriTemplateTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	UriTemplateTestSuite.class,
	ProjectionTestSuite.class
})
public class AllTestSuite {

}
