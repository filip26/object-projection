package com.apicatalog.uritemplate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UriTemplateExpandTest {

    @Test
    public void testPopulateNoVar() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/var1/");
    	final String url = pattern.expand();
    	Assert.assertEquals("https://example.org/var1/", url);
    }

    @Test
    public void testPopulate1Var() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}/");
    	final String url = pattern.expand("XYZ");
    	Assert.assertEquals("https://example.org/XYZ/", url);
    }

    @Test
    public void testPopulate1Var2() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{}");
    	final String url = pattern.expand("XYZ");
    	Assert.assertEquals("https://example.org/XYZ", url);
    }

    @Test
    public void testPopulate2Var() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}/{var2}/test");
    	final String url = pattern.expand("XYZ", "123");
    	Assert.assertEquals("https://example.org/XYZ/123/test", url);
    }

    @Test
    public void testPopulate2Var2() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{}?{}test");
    	final String url = pattern.expand("XYZ", "123");
    	Assert.assertEquals("https://example.org/XYZ?123test", url);
    }

    @Test
    public void testPopulate3Var1() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/{1}/{2}?{3}");
    	final String url = pattern.expand("XYZ", "123", "!@#");
    	Assert.assertEquals("/XYZ/123?!@#", url);
    }

}
