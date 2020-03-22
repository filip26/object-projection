package com.apicatalog.urlpattern;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.ProjectionFactory;

@RunWith(JUnit4.class)
public class UrlPatternTest {

	ProjectionFactory projection;

    @Test
    public void testValueOfNoVar() throws MalformedUrlPattern {
    	
    	final UrlPattern pattern = UrlPattern.valueOf("https://example.org/var1/test");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(1, pattern.elements.length);
    	Assert.assertEquals("https://example.org/var1/test", pattern.elements[0]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(0, pattern.variables.length);
    }
	
    @Test
    public void testValueOf1Var() throws MalformedUrlPattern {
    	
    	final UrlPattern pattern = UrlPattern.valueOf("https://example.org/{var1}/test");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(3, pattern.elements.length);
    	Assert.assertEquals("https://example.org/", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	Assert.assertEquals("/test", pattern.elements[2]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    }

    @Test
    public void testValueOf2Var() throws MalformedUrlPattern {
    	
    	final UrlPattern pattern = UrlPattern.valueOf("https://example.org/{var1}{var2}");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(3, pattern.elements.length);
    	Assert.assertEquals("https://example.org/", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	Assert.assertEquals("var2", pattern.elements[2]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(2, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	Assert.assertEquals(2, pattern.variables[1]);
    }

    @Test
    public void testPopulateNoVar() throws MalformedUrlPattern {
    	final UrlPattern pattern = UrlPattern.valueOf("https://example.org/var1/");
    	final String url = pattern.populate();
    	Assert.assertEquals("https://example.org/var1/", url);
    }

    @Test
    public void testPopulate1Var() throws MalformedUrlPattern {
    	final UrlPattern pattern = UrlPattern.valueOf("https://example.org/{var1}/");
    	final String url = pattern.populate("XYZ");
    	Assert.assertEquals("https://example.org/XYZ/", url);
    }

    @Test
    public void testPopulate2Var() throws MalformedUrlPattern {
    	final UrlPattern pattern = UrlPattern.valueOf("https://example.org/{var1}/{var2}/test");
    	final String url = pattern.populate("XYZ", "123");
    	Assert.assertEquals("https://example.org/XYZ/123/test", url);
    }

}
