package com.apicatalog.uritemplate;

import org.junit.Assert;
import org.junit.Test;

public class UriTemplateExpandTest {

    @Test
    public void testExpandNoVar() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/var1/");
    	final String url = pattern.expand();
    	Assert.assertEquals("https://example.org/var1/", url);
    }

    @Test
    public void testExpand1Var() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}/");
    	final String url = pattern.expand("XYZ");
    	Assert.assertEquals("https://example.org/XYZ/", url);
    }

    @Test
    public void testExpand1Var2() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{}");
    	final String url = pattern.expand("XYZ");
    	Assert.assertEquals("https://example.org/XYZ", url);
    }

    @Test
    public void testExpand2Var() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}/{var2}/test");
    	final String url = pattern.expand("XYZ", "123");
    	Assert.assertEquals("https://example.org/XYZ/123/test", url);
    }

    @Test
    public void testExpand2Var2() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{}?{}test");
    	final String url = pattern.expand("XYZ", "123");
    	Assert.assertEquals("https://example.org/XYZ?123test", url);
    }
    
    @Test
    public void testExpand3Var1() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/{1}/{2}?{3}");
    	final String url = pattern.expand("XYZ", "123", "!@#");
    	Assert.assertEquals("/XYZ/123?!@#", url);
    }

}
