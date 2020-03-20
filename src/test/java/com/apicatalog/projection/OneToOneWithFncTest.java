package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.fnc.InvertibleFunctionError;
import com.apicatalog.projection.scanner.ProjectionScanner;

@RunWith(JUnit4.class)
public class OneToOneWithFncTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		ProjectionIndex index = new ProjectionIndex();
		index.add(scanner.scan(TestProjectionAF.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestObjectA oa = new TestObjectA();
    	oa.stringValue = "ABCDEF";
    	
    	TestProjectionAF pa = projection.compose(TestProjectionAF.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.originString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL"), pa.modifiedString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL").concat("MNOPQR"), pa.modified2xString);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionAF pa = new TestProjectionAF();
    	pa.originString = "ABCDEF";
    	pa.modifiedString = "ABCDEFGHIJKL";
    	pa.modified2xString = "ABCDEFGHIJKLMNOPQR";
    	
    	Object[] oo = projection.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(TestObjectA.class, oo[0].getClass());
    	
    	TestObjectA oa = (TestObjectA)oo[0];

    	Assert.assertEquals("ABCDEF", oa.stringValue);
    }
}
