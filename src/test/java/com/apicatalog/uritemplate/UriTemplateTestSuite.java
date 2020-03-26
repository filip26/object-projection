package com.apicatalog.uritemplate;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	UriTemplateValueOfTest.class,
	UriTemplateExpandTest.class,
	UriTemplateExtractTest.class
})
public class UriTemplateTestSuite {

}
