package com.apicatalog.uritemplate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.uritemplate.MalformedUriTemplate;
import com.apicatalog.uritemplate.UriTemplateL1;

@RunWith(JUnit4.class)
public class UriTemplateExtractTest {

	ProjectionFactory projection;

    @Test
    public void testExtractNoVar() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/var1/");
    	final String[] params = pattern.extract("https://example.org/1/2");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(0, params.length);
    }

    @Test
    public void testExtract1Var() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}/");
    	final String[] params = pattern.extract("https://example.org/123/");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(1, params.length);
    	Assert.assertEquals("123", params[0]);
    }

    @Test
    public void testExtract1Var2() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}");
    	final String[] params = pattern.extract("https://example.org/123");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(1, params.length);
    	Assert.assertEquals("123", params[0]);
    }

    @Test
    public void testExtract1Var3() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("{var1}");
    	final String[] params = pattern.extract("123");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(1, params.length);
    	Assert.assertEquals("123", params[0]);
    }

    @Test
    public void testExtract1Var4() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/{var1}A");
    	final String[] params = pattern.extract("/123A");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(1, params.length);
    	Assert.assertEquals("123", params[0]);
    }

    @Test
    public void testExtract2Var() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/x{var1}y/x{var2}y/x");
    	final String[] params = pattern.extract("https://example.org/x123y/xABCy/x");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(2, params.length);
    	Assert.assertEquals("123", params[0]);
    	Assert.assertEquals("ABC", params[1]);
    }

    @Test
    public void testExtract2Var2() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/x{var1}y/x{var2}");
    	final String[] params = pattern.extract("/x123y/xABC");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(2, params.length);
    	Assert.assertEquals("123", params[0]);
    	Assert.assertEquals("ABC", params[1]);
    }
    
    @Test
    public void testExtract2Var3() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/x{var1}y/x{var2}yyz");
    	final String[] params = pattern.extract("/x123y/xABCyyz");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(2, params.length);
    	Assert.assertEquals("123", params[0]);
    	Assert.assertEquals("ABC", params[1]);
    }

    @Test
    public void testExtract2Var4() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("{var1}y/x{var2}");
    	final String[] params = pattern.extract("123y/xABC");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(2, params.length);
    	Assert.assertEquals("123", params[0]);
    	Assert.assertEquals("ABC", params[1]);
    }

}
