package com.apicatalog.uritemplate;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.ProjectionFactory;

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
    public void testExtract1Var5() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/{var1}A");
    	final String[] params = pattern.extract("/123BBB");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(0, params.length);
    }

    @Test
    public void testExtract1Var6() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/abc/{var1}");
    	final String[] params = pattern.extract("/123/123");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(0, params.length);
    }
    
    @Test
    public void testExtract1Var7() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/abc/{var1}/");
    	final String[] params = pattern.extract("/abc/1");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(0, params.length);
    }

    @Test
    public void testExtract1Var8() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/{var1}x/a");
    	final String[] params = pattern.extract("/123x/b");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(0, params.length);
    }
    
    @Test
    public void testExtract1Var9() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("/{var1}x");
    	final String[] params = pattern.extract("/");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(0, params.length);
    }

    @Test
    public void testExtract1Var10() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("a{var1}x");
    	final String[] params = pattern.extract("ax");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(1, params.length);
    	Assert.assertEquals("", params[0]);
    }

    @Test
    public void testExtract1Var11() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("a{var1}x");
    	final String[] params = pattern.extract("ay");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(0, params.length);
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

    @Test
    public void testExtract2Var5() throws MalformedUriTemplate {
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://www.example.org/{}/{stringValue}");
		final String[] params = pattern.extract("https://www.example.org/123456/ABC");
    	Assert.assertNotNull(params);
    	Assert.assertEquals(2, params.length);
    	Assert.assertEquals("123456", params[0]);
    	Assert.assertEquals("ABC", params[1]);
    }
}
