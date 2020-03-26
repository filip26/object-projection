package com.apicatalog.uritemplate;

import org.junit.Assert;
import org.junit.Test;

public class UriTemplateValueOfTest {

    @Test
    public void testValueOfNoVar() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/var1/test");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(1, pattern.elements.length);
    	Assert.assertEquals("https://example.org/var1/test", pattern.elements[0]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(0, pattern.variables.length);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(0, pattern.stopCharacters.length);
    }

    @Test
    public void testValueOfNull() throws MalformedUriTemplate {
    	try {
    		UriTemplateL1.of(null);
    		Assert.fail();
    		
    	} catch (IllegalArgumentException ignore) {}
    }

    @Test
    public void testValueOfEmpty1() {
    	try {
    		UriTemplateL1.of("");
    		Assert.fail();
    		
    	} catch (MalformedUriTemplate ignore) {}
    }

    @Test
    public void testValueOfEmpty2() throws MalformedUriTemplate {
    	try {
    		UriTemplateL1.of("     ");
    		Assert.fail();
    		
    	} catch (MalformedUriTemplate ignore) {}
    }

    @Test
    public void testValueOf1Var() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}/test");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(4, pattern.elements.length);
    	Assert.assertEquals("https://example.org/", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	Assert.assertEquals("/", pattern.elements[2]);
    	Assert.assertEquals("test", pattern.elements[3]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals('/', pattern.stopCharacters[0]);
    }

    @Test
    public void testValueOf1Var2() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{var1}");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(2, pattern.elements.length);
    	Assert.assertEquals("https://example.org/", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[0]);
    }
    
    @Test
    public void testValueOf1Var3() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/x{var1}y/x");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(4, pattern.elements.length);
    	Assert.assertEquals("https://example.org/x", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	Assert.assertEquals("y/", pattern.elements[2]);
    	Assert.assertEquals("x", pattern.elements[3]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals('/', pattern.stopCharacters[0]);

    }

    @Test
    public void testValueOf1Var4() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("{var1}");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(1, pattern.elements.length);
    	Assert.assertEquals("var1", pattern.elements[0]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(0, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[0]);
    }

    @Test
    public void testValueOf1Var5() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("{var1}xxx");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(2, pattern.elements.length);
    	Assert.assertEquals("var1", pattern.elements[0]);
    	Assert.assertEquals("xxx", pattern.elements[1]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(0, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[0]);
    }

    @Test
    public void testValueOf1Var6() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("{}");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(1, pattern.elements.length);
    	Assert.assertEquals("", pattern.elements[0]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(0, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[0]);
    }


    @Test
    public void testValueOf1Var7() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("yyy{var1}xxx");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(3, pattern.elements.length);
    	Assert.assertEquals("yyy", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	Assert.assertEquals("xxx", pattern.elements[2]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[0]);
    }

    @Test
    public void testValueOf1Var8() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("/{x}   ");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(2, pattern.elements.length);
    	Assert.assertEquals("/", pattern.elements[0]);
    	Assert.assertEquals("x", pattern.elements[1]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[0]);
    }


    @Test
    public void testValueOf1Var9() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("  {}");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(1, pattern.elements.length);
    	Assert.assertEquals("", pattern.elements[0]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(1, pattern.variables.length);
    	Assert.assertEquals(0, pattern.variables[0]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(1, pattern.stopCharacters.length);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[0]);
    }

    @Test
    public void testValueOf2Var() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/{}/{var2}");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(4, pattern.elements.length);
    	Assert.assertEquals("https://example.org/", pattern.elements[0]);
    	Assert.assertEquals("", pattern.elements[1]);
    	Assert.assertEquals("/", pattern.elements[2]);
    	Assert.assertEquals("var2", pattern.elements[3]);
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(2, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	Assert.assertEquals(3, pattern.variables[1]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(2, pattern.stopCharacters.length);
    	Assert.assertEquals('/', pattern.stopCharacters[0]);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[1]);
    }
        
    @Test
    public void testValueOf2Var2() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/x{var1}y/x{var2}y");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(6, pattern.elements.length);
    	Assert.assertEquals("https://example.org/x", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	Assert.assertEquals("y/", pattern.elements[2]);
    	Assert.assertEquals("x", pattern.elements[3]);
    	Assert.assertEquals("var2", pattern.elements[4]);
    	Assert.assertEquals("y", pattern.elements[5]);
    	
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(2, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	Assert.assertEquals(4, pattern.variables[1]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(2, pattern.stopCharacters.length);
    	Assert.assertEquals('/', pattern.stopCharacters[0]);
    	Assert.assertEquals(UriTemplateL1.END_OF_INPUT, pattern.stopCharacters[1]);
    }
    
    @Test
    public void testValueOf2Var3() throws MalformedUriTemplate {
    	
    	final UriTemplateL1 pattern = UriTemplateL1.of("https://example.org/x{var1}y/xx/y{var2}y/");
    	
    	Assert.assertNotNull(pattern);
    	
    	Assert.assertNotNull(pattern.elements);
    	Assert.assertEquals(6, pattern.elements.length);
    	Assert.assertEquals("https://example.org/x", pattern.elements[0]);
    	Assert.assertEquals("var1", pattern.elements[1]);
    	Assert.assertEquals("y/", pattern.elements[2]);
    	Assert.assertEquals("xx/y", pattern.elements[3]);
    	Assert.assertEquals("var2", pattern.elements[4]);
    	Assert.assertEquals("y/", pattern.elements[5]);
    	
    	
    	Assert.assertNotNull(pattern.variables);
    	Assert.assertEquals(2, pattern.variables.length);
    	Assert.assertEquals(1, pattern.variables[0]);
    	Assert.assertEquals(4, pattern.variables[1]);
    	
    	Assert.assertNotNull(pattern.stopCharacters);
    	Assert.assertEquals(2, pattern.stopCharacters.length);
    	Assert.assertEquals('/', pattern.stopCharacters[0]);
    	Assert.assertEquals('/', pattern.stopCharacters[1]);
    }
}
