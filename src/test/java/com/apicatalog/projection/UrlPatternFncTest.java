package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.ifnc.InvertibleFunctionError;
import com.apicatalog.projection.mapping.MappingIndex;
import com.apicatalog.projection.scanner.ProjectionScanner;

@RunWith(JUnit4.class)
public class UrlPatternFncTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		MappingIndex index = new MappingIndex();
		index.add(scanner.scan(TestProjectionUrl.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestObjectA oa = new TestObjectA();
    	oa.longValue = 123l;

    	TestObjectAA oaa = new TestObjectAA();
    	oaa.stringValue = "ABC"; 

    	TestProjectionUrl pa = projection.compose(TestProjectionUrl.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals("https://www.example.org/" + oa.longValue + "/" + oaa.stringValue, pa.href);    	
    }
    
}
