package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.fnc.InvertibleFunctionError;
import com.apicatalog.projection.scanner.ProjectionScanner;

@RunWith(JUnit4.class)
public class EmbeddedOneToOne {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		ProjectionIndex index = new ProjectionIndex();
		index.add(scanner.scan(TestProjectionAA.class));
		index.add(scanner.scan(TestProjectionAI.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestObjectA oa = new TestObjectA();
    	oa.longValue = 123456l;

    	TestObjectAA oaa = new TestObjectAA();
    	oaa.stringValue = "inherit me";
    	oaa.objectA = oa;

    	TestProjectionAA pa = projection.compose(TestProjectionAA.class, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertNotNull(pa.a);
    	
    	Assert.assertEquals(oa.longValue, pa.a.projectedLong);
    	Assert.assertEquals(oaa.stringValue, pa.a.inheritedValue);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionAI pa = new TestProjectionAI();
    	pa.projectedLong = 123456l;
    	pa.inheritedValue = "inherit me";

    	TestProjectionAA paa = new TestProjectionAA();
    	paa.a = pa;

    	Object[] oo = projection.decompose(paa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(TestObjectAA.class, oo[0].getClass());
    	
    	TestObjectAA oaa = (TestObjectAA)oo[0];
    	Assert.assertNotNull(oaa.objectA);

    	Assert.assertEquals(paa.a.projectedLong, oaa.objectA.longValue);
    	Assert.assertEquals(paa.a.inheritedValue, oaa.stringValue);
    }
}

