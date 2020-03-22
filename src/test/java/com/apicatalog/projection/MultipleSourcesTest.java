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
public class MultipleSourcesTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		MappingIndex index = new MappingIndex();
		index.add(scanner.scan(TestProjectionMS.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestObjectA oa = new TestObjectA();
    	oa.longValue = 123l;

    	TestObjectAA oaa = new TestObjectAA();
    	oaa.stringValue = "ABC"; 

    	TestProjectionMS pa = projection.compose(TestProjectionMS.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals(oa.longValue + oaa.stringValue + "!@#", pa.longstring);
    	Assert.assertEquals(oaa.stringValue + oa.longValue, pa.stringlong);    	
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionMS pa = new TestProjectionMS();
    	pa.longstring = "123ABC";
    	pa.stringlong = "ABC123";
    	
    	Object[] oo = projection.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(2, oo.length);
    	Assert.assertNotNull(oo[0]);
    	Assert.assertEquals(TestObjectA.class, oo[0].getClass());
    	Assert.assertNotNull(oo[1]);
    	Assert.assertEquals(TestObjectAA.class, oo[1].getClass());
    	
    	TestObjectA oa = (TestObjectA)oo[0];
    	//TODO
    }
}
